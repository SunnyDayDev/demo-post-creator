package dev.sunnyday.postcreator.postcreator

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import dagger.android.support.DaggerFragment
import dev.sunnyday.postcreator.drawablechooser.DrawableItem
import dev.sunnyday.postcreator.drawablechooser.DrawableChooserListener
import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.domain.backgrounds.Background
import dev.sunnyday.postcreator.domain.backgrounds.resolver.BackgroundResolver
import dev.sunnyday.postcreator.domain.backgrounds.BackgroundsRepository
import dev.sunnyday.postcreator.postcreator.saver.ViewAsImageSaver
import dev.sunnyday.postcreator.stickersboard.Sticker
import dev.sunnyday.postcreator.stickersboard.StickersBoard
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.postcreator__fragment.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.min

class PostCreatorFragment : DaggerFragment() {

    @Inject
    internal lateinit var backgroundsRepository: BackgroundsRepository

    @Inject
    internal lateinit var backgroundsResolver: BackgroundResolver

    @Inject
    internal lateinit var asImageSaver: ViewAsImageSaver

    @Inject
    internal lateinit var schedulers: AppSchedulers

    private var backgroundsMap = mapOf<Long, Background>()
    private var drawableItemsMap = mapOf<Background, DrawableItem>()

    private val dispose = CompositeDisposable()

    private val textStyleSwitcher: TextStyleSwitcher by lazy {
        TextStyleSwitcher(context!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.postcreator__fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCreatorSize()
        setupTextStyleSwitcher()
        setupBackgrounds()
        setupStickers()
        setupSaveButton()
    }

    private fun updateCreatorSize() {
        val display = resources.displayMetrics
        val smallestSize = min(display.widthPixels, display.heightPixels)

        creatorView.updateLayoutParams {
            width = smallestSize
        }
    }

    private fun setupTextStyleSwitcher() {
        textStyleSwitcher.applyNextStyle(creatorView)

        switchTextStyleButton.setOnClickListener {
            textStyleSwitcher.applyNextStyle(creatorView)
        }
    }

    private fun setupBackgrounds() {
        backgroundsRepository.backgrounds()
            .observeOn(schedulers.ui)
            .subscribeBy(onNext = this::handleBackgrounds)
            .let(dispose::add)

        drawableChooser.addListener(object : DrawableChooserListener {

            override fun onSelected(item: DrawableItem) {
                val background = backgroundsMap[item.tag] ?: return
                onBackgroundChoosed(background)
            }

        })
    }

    private fun handleBackgrounds(backgrounds: List<Background>) {
        val currentSelected = drawableChooser.selectedPosition
            .let { drawableChooser.items.getOrNull(it) }
            ?.let { backgroundsMap[it.tag] }

        backgroundsMap = backgrounds.associateBy { it.id }

        val drawableItemsMap = backgrounds.associateWith {
            drawableItemsMap[it] ?: chooserItemForBackground(it)
        }
        this.drawableItemsMap = drawableItemsMap

        drawableChooser.items = backgrounds.mapNotNull(drawableItemsMap::get)
        drawableChooser.selectedPosition = currentSelected?.let(backgrounds::indexOf) ?: 0
    }

    private fun onBackgroundChoosed(background: Background) {
        val context = context ?: return

        creatorView.background = backgroundsResolver.resolve(background)

        if (background is Background.Color && background.color == Color.WHITE) {
            val actionsBorderWidth = Dimen.dp(2, context).toInt()
            creatorView.setActionsBorderWidth(actionsBorderWidth)
        } else {
            creatorView.setActionsBorderWidth(0)
        }
    }

    private fun chooserItemForBackground(background: Background): DrawableItem =
        DrawableItem(background.id, source = {
            backgroundsResolver.resolveIcon(background) ?: ColorDrawable(Color.TRANSPARENT)
        })

    private fun setupStickers() {
        stickersButton.setOnClickListener {
            val context = context ?: return@setOnClickListener

            val stickers = (1L..24L).map {
                Sticker(it, Uri.parse("file:///android_asset/stickers/$it.png"))
            }

            StickersBoard.show(context, stickers) {
                creatorView.addImage(it.uri)
            }
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            creatorView.isEnabled = false

            asImageSaver.save(creatorView)
                .observeOn(schedulers.ui)
                .doFinally { creatorView.isEnabled = true }
                .subscribeBy(
                    onComplete = { Timber.d("Storage permissions granged") },
                    onError = { Timber.d("Error: $it")  }
                )
                .let(dispose::add)
        }
    }

}