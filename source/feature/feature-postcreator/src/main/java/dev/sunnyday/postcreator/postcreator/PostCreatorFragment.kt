package dev.sunnyday.postcreator.postcreator

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.LongSparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.core.util.keyIterator
import androidx.core.util.set
import androidx.core.util.valueIterator
import androidx.core.view.updateLayoutParams
import dagger.android.support.DaggerFragment
import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.core.common.android.attachToLifecycle
import dev.sunnyday.postcreator.core.permissions.PermissionsNotGrantedError
import dev.sunnyday.postcreator.domain.backgrounds.Background
import dev.sunnyday.postcreator.domain.backgrounds.BackgroundsRepository
import dev.sunnyday.postcreator.domain.backgrounds.resolver.BackgroundResolver
import dev.sunnyday.postcreator.domain.stickers.Sticker
import dev.sunnyday.postcreator.domain.stickers.StickersRepository
import dev.sunnyday.postcreator.drawablechooser.DrawableChooserListener
import dev.sunnyday.postcreator.drawablechooser.DrawableItem
import dev.sunnyday.postcreator.postcreator.operation.AddBackgroundFromDeviceOperation
import dev.sunnyday.postcreator.postcreator.operation.DrawViewToFileOperation
import dev.sunnyday.postcreator.postcreator.styles.TextStyleSwitcher
import dev.sunnyday.postcreator.postcreatorboard.PostCreatorBoardView
import dev.sunnyday.postcreator.postcreatorboard.PostCreatorImage
import dev.sunnyday.postcreator.stickersboard.StickerBoardItem
import dev.sunnyday.postcreator.stickersboard.StickersBoard
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.postcreator__fragment.*
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject
import kotlin.math.min


class PostCreatorFragment : DaggerFragment() {

    @Inject
    internal lateinit var backgroundsRepository: BackgroundsRepository

    @Inject
    internal lateinit var stickersRepository: StickersRepository

    @Inject
    internal lateinit var backgroundsResolver: BackgroundResolver

    @Inject
    internal lateinit var saveOperation: DrawViewToFileOperation

    @Inject
    internal lateinit var addBackgroundOperation: AddBackgroundFromDeviceOperation

    @Inject
    internal lateinit var textStyleSwitcher: TextStyleSwitcher

    @Inject
    internal lateinit var schedulers: AppSchedulers

    private var backgrounds = LongSparseArray<Background>()
    private var backgroundsChooserItems = LongSparseArray<DrawableItem>()
    private var selectedBackgroundId: Long? = null

    private val dispose = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSavedStateManaging()
        setupBackgroundsFetching()
    }

    private fun setupSavedStateManaging() {
        setupSavedStateManaging(
            SAVED_STATE_FRAGMENT,
            restoreState = { state ->
                selectedBackgroundId = state.getLong(KEY_SELECTED_BACKGROUND_ID)
                    .takeIf { it != -1L }
                textStyleSwitcher.activeTextStyleIndex = state.getInt(KEY_TEXT_STYLE_INDEX)
            },
            saveState = {
                putLong(KEY_SELECTED_BACKGROUND_ID, selectedBackgroundId ?: -1L)
                putInt(KEY_TEXT_STYLE_INDEX, textStyleSwitcher.activeTextStyleIndex)
            })
    }

    private fun setupBackgroundsFetching() {
        backgroundsRepository.backgrounds()
            .observeOn(schedulers.ui)
            .subscribeBy(onNext = this::handleBackgrounds)
            .let(dispose::add)
    }

    private fun handleBackgrounds(fetchedBackgrounds: List<Background>) {
        backgrounds.clear()
        fetchedBackgrounds.forEach { backgrounds[it.id] = it }

        removeUnexistsDrawableItems(fetchedBackgrounds)
        fetchedBackgrounds.forEach {
            val item = backgroundsChooserItems[it.id] ?: chooserItemForBackground(it)
            backgroundsChooserItems[it.id] = item
        }

        if (!fetchedBackgrounds.any { it.id == selectedBackgroundId }) {
            selectedBackgroundId = fetchedBackgrounds.firstOrNull()?.id
        }

        applyBackgroundsToDrawableChooser()
    }

    private fun removeUnexistsDrawableItems(backgrounds: List<Background>) {
        val existsIds = backgrounds.map { it.id }

        backgroundsChooserItems.keyIterator()
            .asSequence()
            .filterNot(existsIds::contains)
            .toList()
            .forEach(backgroundsChooserItems::remove)
    }

    private fun applyBackgroundsToDrawableChooser() {
        view ?: return

        val items = backgroundsChooserItems.valueIterator().asSequence().toList()

        drawableChooser.items = items

        val selectedId = selectedBackgroundId
        if (selectedId == null) {
            drawableChooser.selectedPosition = 0
        } else {
            drawableChooser.selectedPosition = items.indexOf(backgroundsChooserItems[selectedId])
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.postcreator__fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCreatorViewSize()
        setupCreatorViewSavedStateManaging()
        setupTextStyleSwitcher()
        setupDrawableChooser()
        setupStickersButton()
        setupSaveButton()
        setupScrollableContent()
    }

    private fun updateCreatorViewSize() {
        val display = resources.displayMetrics
        val smallestSize = min(display.widthPixels, display.heightPixels)

        creatorView.updateLayoutParams {
            width = smallestSize
        }
    }

    private fun setupCreatorViewSavedStateManaging() {
        setupSavedStateManaging(
            SAVED_STATE_CREATOR_VIEW,
            restoreState = {
                it.getParcelableArrayList<PostCreatorImage>(KEY_IMAGES)
                    ?.forEach(creatorView::addImage)
            },
            saveState = {
                putParcelableArrayList(KEY_IMAGES, ArrayList(creatorView.images))
            })
    }

    private fun setupTextStyleSwitcher() {
        textStyleSwitcher.applyStyle(creatorView)

        switchTextStyleButton.setOnClickListener {
            textStyleSwitcher.applyNextStyle(creatorView)
        }
    }

    private fun setupDrawableChooser() {
        drawableChooser.addListener(object : DrawableChooserListener {
            override fun onSelected(item: DrawableItem) = onBackgroundDrawableItemSelected(item)
            override fun onAddClick() = onAddBackgroundRequested()
        })
    }

    private fun onBackgroundDrawableItemSelected(item: DrawableItem) {
        val id = item.tag
        val background = backgrounds[id] ?: return

        selectedBackgroundId = id
        creatorView.background = backgroundsResolver.resolve(background)

        updateActionsStyleByBackground(background)
    }

    private fun updateActionsStyleByBackground(background: Background) {
        val context = context ?: return

        if (background is Background.Color && background.color == Color.WHITE) {
            val actionsBorderWidth = Dimen.dp(2, context).toInt()
            creatorView.setActionsBorderWidth(actionsBorderWidth)
        } else {
            creatorView.setActionsBorderWidth(0)
        }
    }

    private fun onAddBackgroundRequested() {
        addBackgroundOperation.execute()
            .subscribeBy(onError = this::checkPermissionError)
            .let(dispose::add)
    }

    private fun chooserItemForBackground(background: Background): DrawableItem =
        DrawableItem(background.id, source = {
            backgroundsResolver.resolveIcon(background) ?: ColorDrawable(Color.TRANSPARENT)
        })

    private fun setupStickersButton() {
        stickersButton.setOnClickListener {
            showStickersBoard()
        }
    }

    private fun showStickersBoard() {
        stickersRepository.stickers()
            .observeOn(schedulers.ui)
            .subscribeBy(onSuccess = this::showStickersBoard)
            .let(dispose::add)
    }

    private fun showStickersBoard(stickers: List<Sticker>) {
        val context = context ?: return

        val stickerBoardItems = stickers.map { StickerBoardItem(it.sourceUri) }
        val stickerRect = getNewStickerRect()

        val stickersBoard = StickersBoard.show(
            context, stickerBoardItems,
            stickerRectProvider = { getStickerRectInWindow(stickerRect) },
            callback = {
                val postImage = PostCreatorImage(it.uri, stickerRect)
                creatorView.addImage(postImage)
            })

        stickersBoard.attachToLifecycle(lifecycle)
    }

    private fun getNewStickerRect(): Rect {
        val context = context ?: return Rect()

        val size = Dimen.dp(92, context).toInt()
        val left = (creatorView.width - size) / 2

        return Rect(left, 0, left + size, size)
    }

    private fun getStickerRectInWindow(stickerRect: Rect): Rect {
        val activity = activity ?: return Rect()

        val stickerWindowRect = Rect()
        creatorView.getGlobalVisibleRect(stickerWindowRect)
        stickerWindowRect.offset(stickerRect.left, stickerRect.top - scrollableContent.scrollY)
        stickerWindowRect.right = stickerWindowRect.left + stickerRect.width()
        stickerWindowRect.bottom = stickerWindowRect.top + stickerRect.height()

        val decorViewRect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(decorViewRect)
        stickerWindowRect.offset(0, -decorViewRect.top)

        return stickerWindowRect
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            savePost()
        }

        updateSaveButtonState()
        creatorView.addTextChangedListener(object : PostCreatorBoardView.TextChangedListener {
            override fun onTextChanged(text: String) = updateSaveButtonState()
        })
    }

    private fun savePost() {
        view?.requestFocus()
        creatorView.isEnabled = false

        hideKeyboard()

        saveOperation.drawToFile(creatorView)
            .observeOn(schedulers.ui)
            .doFinally { creatorView.isEnabled = true }
            .subscribeBy(onError = this::checkPermissionError)
            .let(dispose::add)
    }

    private fun hideKeyboard() {
        val activity = this.activity
            ?: return

        val inputMethodManager: InputMethodManager = activity.getSystemService()
            ?: return

        val focusedView = activity.currentFocus?.windowToken
            ?: return

        inputMethodManager.hideSoftInputFromWindow(focusedView, 0)
    }

    private fun updateSaveButtonState() {
        val shouldBeEnabled = creatorView.text.isNotEmpty()
        if (shouldBeEnabled != saveButton.isEnabled) {
            saveButton.isEnabled = shouldBeEnabled
            saveButton.alpha = if (shouldBeEnabled) 1.0f else 0.48f
        }
    }

    private fun setupScrollableContent() {
        creatorView.addImageStateListener(object : PostCreatorBoardView.ImageStateListener {

            override fun onStartTrackingImage() = setScrollingEnabled(false)

            override fun onStopTrackingImage() = setScrollingEnabled(true)

        })
    }

    private fun setScrollingEnabled(isEnabled: Boolean) {
        scrollableContent.isScrollEnabled = isEnabled
    }

    private fun checkPermissionError(error: Throwable) {
        if (error is PermissionsNotGrantedError) {
            showMessageDialog(R.string.postcreator__prompt__permission_not_granted_error)
        } else {
            Timber.e(error)
        }
    }

    private fun showMessageDialog(@StringRes textId: Int) {
        val context = context ?: return

        AlertDialog.Builder(context)
            .setMessage(textId)
            .setPositiveButton(android.R.string.ok, null)
            .show()
            .attachToLifecycle(lifecycle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        savedStateRegistry.unregisterSavedStateProvider(SAVED_STATE_CREATOR_VIEW)
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose.clear()
    }

    private fun setupSavedStateManaging(
        name: String,
        restoreState: (Bundle) -> Unit,
        saveState: Bundle.() -> Unit
    ) {
        savedStateRegistry.apply {
            consumeRestoredStateForKey(name)?.let(restoreState)
            registerSavedStateProvider(name) {
                Bundle().apply(saveState)
            }
        }
    }

    companion object {

        private const val SAVED_STATE_CREATOR_VIEW = "postCreatorView"
        private const val SAVED_STATE_FRAGMENT = "postCreatorFragment"


        private const val KEY_IMAGES = "images"
        private const val KEY_SELECTED_BACKGROUND_ID = "selectedBackgroundId"
        private const val KEY_TEXT_STYLE_INDEX = "textStyleIndex"

    }

}
