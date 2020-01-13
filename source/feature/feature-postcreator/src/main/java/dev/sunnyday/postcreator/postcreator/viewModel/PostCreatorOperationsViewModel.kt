package dev.sunnyday.postcreator.postcreator.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.postcreator.operation.AddBackgroundFromDeviceOperation
import dev.sunnyday.postcreator.postcreator.operation.SavePostOperation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

internal class PostCreatorOperationsViewModel @Inject constructor(
    private val saveOperation: SavePostOperation,
    private val addBackgroundOperation: AddBackgroundFromDeviceOperation,
    private val schedulers: AppSchedulers
) : ViewModel() {

    private val dispose = CompositeDisposable()

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

    override fun onCleared() {
        super.onCleared()
        dispose.dispose()
    }

}