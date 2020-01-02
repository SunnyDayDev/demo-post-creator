package dev.sunnyday.postcreator.core.activityforresult

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import dev.sunnyday.postcreator.core.ui.ActivityObserver
import io.reactivex.Maybe
import io.reactivex.disposables.Disposables
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

internal class ActivityForResultRequestInteractorImpl @Inject constructor(
    private val activityObserver: ActivityObserver
) : ActivityForResultRequestInteractor {

    override fun <T: Any> startActivityForResult(request: ActivityForResultRequest<T>): Maybe<T> =
        activityObserver.lastStartedActivity
            .filter { (activity) -> activity is FragmentActivity }
            .firstOrError()
            .map { (activity) -> activity as FragmentActivity }
            .flatMapMaybe { proceedRequest(it, request) }

    private fun <T: Any> proceedRequest(
        activity: FragmentActivity,
        request: ActivityForResultRequest<T>
    ) = Maybe.create<T> { emitter ->
        val requestFragment = ActivityForResultRequestFragment.create(request,
            onResult = {
                if (it == null) emitter.onComplete()
                else emitter.onSuccess(it)
            },
            onError = { emitter.tryOnError(it) })

        try {
            val tag = "requestActivityForResult:${UUID.randomUUID()}"

            activity.supportFragmentManager.commit {
                add(requestFragment, tag)
            }

            val weakRequestFragment = WeakReference(requestFragment)
            emitter.setDisposable(Disposables.fromAction {
                weakRequestFragment.get()?.dismiss()
            })
        } catch (e: Throwable) {
            emitter.tryOnError(e)
        }
    }

}