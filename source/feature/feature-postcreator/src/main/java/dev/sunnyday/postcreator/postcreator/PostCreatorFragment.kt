package dev.sunnyday.postcreator.postcreator

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.LongSparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.util.keyIterator
import androidx.core.util.set
import androidx.core.util.valueIterator
import androidx.core.view.drawToBitmap
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import dagger.android.support.DaggerFragment
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.domain.backgrounds.Background
import dev.sunnyday.postcreator.domain.backgrounds.resolver.BackgroundResolver
import dev.sunnyday.postcreator.drawablechooser.DrawableChooserListener
import dev.sunnyday.postcreator.drawablechooser.DrawableItem
import dev.sunnyday.postcreator.postcreator.viewModel.PostCreatorViewModel
import dev.sunnyday.postcreator.postcreator.di.factory.PostCreatorViewModelFactory
import dev.sunnyday.postcreator.postcreator.styles.DecoratedTextStyle
import dev.sunnyday.postcreator.postcreator.viewModel.PostCreatorViewInterface
import dev.sunnyday.postcreator.postcreatorboard.PostCreatorBoardView
import dev.sunnyday.postcreator.postcreatorboard.PostCreatorImage
import dev.sunnyday.postcreator.stickersboard.StickerBoardItem
import kotlinx.android.synthetic.main.postcreator__fragment.*
import javax.inject.Inject
import kotlin.math.min


class PostCreatorFragment : DaggerFragment() {

    @Inject
    internal lateinit var backgroundsResolver: BackgroundResolver

    @Inject
    internal lateinit var viewModelFactoryFactory: PostCreatorViewModelFactory.Factory

    private val viewModel by lazy<PostCreatorViewModel> {
        val viewModelFactory = viewModelFactoryFactory.create(this)
        ViewModelProvider(this, viewModelFactory).get()
    }
    
    private val viewInterface = ViewInterface()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.postcreator__fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCreatorViewSize()
        setupPostCreatorBoardViewSavedStateManaging()
        setupTextStyleSwitcher()
        setupDrawableChooser()
        setupStickersButton()
        setupSaveButton()
        setupScrollableContent()

        viewModel.view = viewInterface
    }

    private fun setupPostCreatorBoardViewSavedStateManaging() {
        savedStateRegistry.registerSavedStateProvider(SAVED_STATE__CREATOR_VIEW) {
            Bundle().apply {
                putParcelableArrayList(SAVED_STATE__KEY__IMAGES, ArrayList(creatorView.images))
            }
        }

        savedStateRegistry.consumeRestoredStateForKey(SAVED_STATE__CREATOR_VIEW)
            ?.getParcelableArrayList<PostCreatorImage>(SAVED_STATE__KEY__IMAGES)
            ?.forEach(creatorView::addImage)
    }

    private fun updateCreatorViewSize() {
        val display = resources.displayMetrics
        val smallestSize = min(display.widthPixels, display.heightPixels)

        creatorView.updateLayoutParams {
            width = smallestSize
        }
    }

    private fun setupTextStyleSwitcher() {
        switchTextStyleButton.setOnClickListener {
            viewModel.changeTextStyle()
        }
    }

    private fun setupDrawableChooser() {
        drawableChooser.addListener(object : DrawableChooserListener {
            override fun onSelected(item: DrawableItem) = viewModel.onBackgroundSelected(item.tag)
            override fun onAddClick() = viewModel.onAddBackground()
        })
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

    private fun setupStickersButton() {
        stickersButton.setOnClickListener {
            viewModel.chooseSticker()
        }
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

        val postBitmap = creatorView.drawToBitmap(Bitmap.Config.ARGB_8888)
        creatorView.isEnabled = true

        viewModel.savePostImage(postBitmap)
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

    override fun onDestroyView() {
        viewModel.view = null

        super.onDestroyView()

        savedStateRegistry.unregisterSavedStateProvider(SAVED_STATE__CREATOR_VIEW)
    }

    internal inner class ViewInterface: PostCreatorViewInterface {

        private val backgroundsChooserItems = LongSparseArray<DrawableItem>()
        
        override fun applyTextStyle(style: DecoratedTextStyle) {
            creatorView.textColor = style.textColor
            creatorView.setTextDecorators(style.decorators)
        }

        override fun getResultStickerBoardItemRect(): Rect {
            val stickerRect = getNewStickerRect()
            return getStickerRectInWindow(stickerRect)
        }

        override fun setSelectedBackground(background: Background) {
            creatorView.background = backgroundsResolver.resolve(background)
            updateActionsStyleByBackground(background)

            val selectedPosition = backgroundsChooserItems[background.id]
                ?.let(backgroundsChooserItems::indexOfValue)
                ?: -1

            drawableChooser.selectedPosition = selectedPosition
        }

        override fun setAvailableBackgrounds(backgrounds: List<Background>) {
            removeUnexistsDrawableItems(backgrounds)
            backgrounds.forEach {
                val item = backgroundsChooserItems[it.id] ?: chooserItemForBackground(it)
                backgroundsChooserItems[it.id] = item
            }

            drawableChooser.items = backgroundsChooserItems
                .valueIterator()
                .asSequence()
                .toList()
        }

        private fun removeUnexistsDrawableItems(backgrounds: List<Background>) {
            val existsIds = backgrounds.map { it.id }

            backgroundsChooserItems.keyIterator()
                .asSequence()
                .filterNot(existsIds::contains)
                .toList()
                .forEach(backgroundsChooserItems::remove)
        }

        private fun chooserItemForBackground(background: Background): DrawableItem =
            DrawableItem(background.id, source = {
                backgroundsResolver.resolveIcon(background) ?: ColorDrawable(Color.TRANSPARENT)
            })

        override fun onStickerBoardItemSelected(item: StickerBoardItem) {
            val stickerRect = getNewStickerRect()
            val postImage = PostCreatorImage(item.uri, stickerRect)
            creatorView.addImage(postImage)
        }
        
    }

    companion object {

        private const val SAVED_STATE__CREATOR_VIEW = "creatorView"
        private const val SAVED_STATE__KEY__IMAGES = "images"

    }

}
