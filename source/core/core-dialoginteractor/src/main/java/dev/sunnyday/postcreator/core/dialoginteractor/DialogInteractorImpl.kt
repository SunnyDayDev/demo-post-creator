package dev.sunnyday.postcreator.core.dialoginteractor

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import dev.sunnyday.postcreator.core.activitytracker.ActivityTrackerObserver
import dev.sunnyday.postcreator.core.common.android.attachToLifecycle
import dev.sunnyday.postcreator.core.common.util.Optional
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposables
import javax.inject.Inject

internal class DialogInteractorImpl @Inject constructor(
    private val activityObserver: ActivityTrackerObserver
) : DialogInteractor {

    override fun showMessage(message: String): Completable =
        activityObserver.lastStartedActivity
            .map { (activity) -> Optional(activity as? FragmentActivity) }
            .switchMapSingle { (activity) ->
                activity ?: return@switchMapSingle Single.never<Unit>()
                showMessage(message, activity)
            }
            .firstOrError()
            .ignoreElement()

    private fun showMessage(
        message: String,
        activity: FragmentActivity
    ): Single<Unit> = Single.create { emitter ->
        val dialog = AlertDialog.Builder(activity)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .setOnDismissListener {
                if (!emitter.isDisposed) {
                    emitter.onSuccess(Unit)
                }
            }
            .show()

        dialog.attachToLifecycle(activity.lifecycle)

        emitter.setDisposable(Disposables.fromAction {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        })
    }

}