package dev.sunnyday.postcreator.core.snackbarinteractor

import android.app.Activity
import android.view.View
import com.google.android.material.snackbar.Snackbar
import dev.sunnyday.postcreator.core.activitytracker.ActivityTrackerObserver
import io.reactivex.Maybe
import io.reactivex.disposables.Disposables
import javax.inject.Inject

internal class SnackbarInteractorImpl @Inject constructor(
    private val activityObserver: ActivityTrackerObserver
) : SnackbarInteractor {

    override fun showMessageWithAction(message: String, actionText: String): Maybe<Unit> =
        activityObserver.lastStartedActivity
            .filter { (activity) -> activity != null }
            .firstElement()
            .flatMap { (activity) ->
                activity ?: return@flatMap Maybe.empty<Unit>()
                showMessageWithAction(message, actionText, activity)
            }

    private fun showMessageWithAction(
        message: String,
        actionText: String,
        activity: Activity
    ): Maybe<Unit> {
        val view = activity.findViewById<View>(android.R.id.content)
            ?: return Maybe.empty<Unit>()

        return Maybe.create<Unit> { emitter ->
            val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(actionText) {
                    emitter.onSuccess(Unit)
                }
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                        if (!emitter.isDisposed) {
                            emitter.onComplete()
                        }
                    }
                })

            emitter.setDisposable(Disposables.fromAction {
                if (snackBar.isShown) {
                    snackBar.dismiss()
                }
            })

            snackBar.show()
        }
    }

}