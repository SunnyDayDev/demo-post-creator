package dev.sunnyday.postcreator.core.dialoginteractor

import android.app.Dialog
import androidx.fragment.app.FragmentActivity
import dev.sunnyday.postcreator.core.activitytracker.ActivityTrackerObserver
import dev.sunnyday.postcreator.core.common.android.attachToLifecycle
import dev.sunnyday.postcreator.core.common.util.Optional
import io.reactivex.*
import io.reactivex.disposables.Disposables
import javax.inject.Inject

internal class DialogInteractorManagerImpl @Inject constructor(
    private val activityObserver: ActivityTrackerObserver
) : DialogInteractorManager {

    override fun <T> showDialog(factory: DialogFactory<T>): Maybe<T> =
        activityObserver.lastStartedActivity
            .map { (activity) -> Optional(activity as? FragmentActivity) }
            .switchMapSingle { (activity) ->
                activity ?: return@switchMapSingle Single.never<Optional<T>>()
                showDialog(factory, activity)
                    .map { Optional(it) }
                    .switchIfEmpty(Single.just(Optional()))
            }
            .firstOrError()
            .flatMapMaybe { (result) ->
                if (result != null) Maybe.just(result)
                else Maybe.empty()
            }

    private fun <T> showDialog(
        factory: DialogFactory<T>,
        activity: FragmentActivity
    ): Maybe<T> = Maybe.create { emitter ->
        var shownDialog: Dialog? = null

        activity.runOnUiThread {
            if (emitter.isDisposed) return@runOnUiThread

            val dialog = factory(activity, emitter)
                .also { shownDialog = it }

            if (dialog.ownerActivity == null) {
                dialog.setOwnerActivity(activity)
            }

            if (!dialog.isShowing) {
                dialog.show()
            }

            dialog.attachToLifecycle(activity.lifecycle, onDismiss = { isManualDismiss ->
                if (isManualDismiss) {
                    emitter.onComplete()
                }
            })
        }

        emitter.setDisposable(Disposables.fromAction {
            val dialog = shownDialog ?: return@fromAction

            if (dialog.isShowing) {
                dialog.dismiss()
            }
        })
    }

}