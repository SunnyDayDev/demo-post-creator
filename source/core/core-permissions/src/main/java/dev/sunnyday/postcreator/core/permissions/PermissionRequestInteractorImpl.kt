package dev.sunnyday.postcreator.core.permissions

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import dev.sunnyday.postcreator.core.activitytracker.ActivityTrackerObserver
import io.reactivex.Completable
import io.reactivex.disposables.Disposables
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PermissionRequestInteractorImpl @Inject constructor(
    private val activityObserver: ActivityTrackerObserver
) : PermissionRequestInteractor {

    override fun requirePermission(request: PermissionRequest): Completable =
        activityObserver.lastStartedActivity
            .filter { (activity) -> activity is FragmentActivity }
            .firstOrError()
            .map { (activity) -> activity as FragmentActivity }
            .flatMapCompletable { requestPermission(it, request) }

    private fun requestPermission(
        activity: FragmentActivity,
        request: PermissionRequest
    ) = Completable.create { emitter ->
        val requestFragment = PermissionRequestFragment.create(request,
            onSuccess = emitter::onComplete,
            onError = { emitter.tryOnError(it) })

        try {
            val tag = "requestPermission:${UUID.randomUUID()}"

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