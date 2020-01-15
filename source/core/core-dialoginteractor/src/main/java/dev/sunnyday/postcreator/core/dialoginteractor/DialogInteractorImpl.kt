package dev.sunnyday.postcreator.core.dialoginteractor

import androidx.appcompat.app.AlertDialog
import io.reactivex.Completable
import javax.inject.Inject

internal class DialogInteractorImpl @Inject constructor(
    private val manager: DialogInteractorManager
) : DialogInteractor {

    override fun showMessage(message: String): Completable = manager
        .showDialog<Nothing> { activity, emitter ->
            AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    emitter.onComplete()
                }
                .create()
        }
        .ignoreElement()

}