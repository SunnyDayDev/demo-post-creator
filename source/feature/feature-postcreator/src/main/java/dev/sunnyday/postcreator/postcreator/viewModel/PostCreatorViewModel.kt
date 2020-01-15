package dev.sunnyday.postcreator.postcreator.viewModel

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.core.app.util.restorable
import dev.sunnyday.postcreator.core.common.util.weak
import dev.sunnyday.postcreator.domain.backgrounds.Background
import dev.sunnyday.postcreator.domain.backgrounds.BackgroundsRepository
import dev.sunnyday.postcreator.postcreator.di.factory.ViewModelAssistedFactory
import dev.sunnyday.postcreator.postcreator.operation.ChooseStickerOperation
import dev.sunnyday.postcreator.postcreator.operation.AddBackgroundFromDeviceOperation
import dev.sunnyday.postcreator.postcreator.operation.SavePostOperation
import dev.sunnyday.postcreator.postcreator.styles.TextStylesSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

internal class PostCreatorViewModel @AssistedInject constructor(
    private val backgroundsRepository: BackgroundsRepository,
    private val textStylesSource: TextStylesSource,
    private val chooseStickerOperation: ChooseStickerOperation,
    private val saveOperation: SavePostOperation,
    private val addBackgroundOperation: AddBackgroundFromDeviceOperation,
    private val schedulers: AppSchedulers,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var view: PostCreatorViewInterface? by weak {
        it ?: return@weak
        onSetView()
    }

    private var activeTextStyleIndex: Int by restorable(0, savedStateHandle)

    private var backgrounds: Map<Long, Background> = emptyMap()
    private var selectedBackgroundId: Long? by restorable(savedStateHandle)

    private val dispose = CompositeDisposable()

    init {
        setupBackgroundsFetching()
    }

    private fun onSetView() {
        updateViewTextStyle()
        updateViewBackgrounds()
        updateViewSelectedBackground()
    }

    private fun setupBackgroundsFetching() {
        backgroundsRepository.backgrounds()
            .observeOn(schedulers.ui)
            .subscribeBy(onNext = this::handleBackgrounds)
            .let(dispose::add)
    }

    private fun handleBackgrounds(fetchedBackgrounds: List<Background>) {
        this.backgrounds = fetchedBackgrounds.associateBy { it.id }

        if (selectedBackgroundId == null || isNotContainsSelectedBackground(fetchedBackgrounds)) {
            selectedBackgroundId = fetchedBackgrounds.firstOrNull()?.id
        }

        updateViewBackgrounds()
        updateViewSelectedBackground()
    }

    private fun updateViewBackgrounds() = withView {
        val backgrounds = backgrounds.values.toList()
        it.setAvailableBackgrounds(backgrounds)
    }

    private fun updateViewSelectedBackground() = withView {
        if (backgrounds.isEmpty()) return@withView

        val selectedBackground = backgrounds[selectedBackgroundId] ?: return@withView
        it.setSelectedBackground(selectedBackground)
    }

    private fun isNotContainsSelectedBackground(fetchedBackgrounds: List<Background>): Boolean =
        !fetchedBackgrounds.any { it.id == selectedBackgroundId }

    fun onBackgroundSelected(id: Long) {
        if (id != selectedBackgroundId && backgrounds.containsKey(id)) {
            selectedBackgroundId = id
            updateViewSelectedBackground()
        }
    }

    fun chooseSticker() {
        chooseStickerOperation
            .chooseSticker(stickerRectProvider = {
                view?.getResultStickerBoardItemRect() ?: Rect()
            })
            .subscribeBy {
                view?.onStickerBoardItemSelected(it)
            }
            .let(dispose::add)
    }

    fun savePostImage(bitmap: Bitmap) {
        saveOperation.savePostImage(bitmap)
            .observeOn(schedulers.ui)
            .subscribeBy()
            .let(dispose::add)
    }

    fun onAddBackground() {
        addBackgroundOperation.execute()
            .subscribeBy()
            .let(dispose::add)
    }

    fun changeTextStyle() {
        activeTextStyleIndex = (activeTextStyleIndex + 1) % textStylesSource.getStylesCount()
        updateViewTextStyle()
    }

    private fun updateViewTextStyle() = withView {
        val style = textStylesSource.getStyle(activeTextStyleIndex) ?: return
        it.applyTextStyle(style)
    }

    override fun onCleared() {
        super.onCleared()
        dispose.dispose()
    }

    private inline fun withView(action: (PostCreatorViewInterface) -> Unit) {
        view?.let(action)
    }

    @AssistedInject.Factory
    internal interface Factory: ViewModelAssistedFactory<PostCreatorViewModel>

}