package dev.sunnyday.postcreator.core.permissions

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import dev.sunnyday.postcreator.core.ui.ActivityObserver
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.disposables.Disposables
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Handle situation when fragment is destroyed before call callback

@Singleton
internal class PermissionRequestInteractorImpl @Inject constructor(
    private val activityObserver: ActivityObserver
) : PermissionRequestInteractor {

    override fun requirePermission(request: PermissionRequest): Completable =
        activityObserver.lastStartedActivity
            .filter { (activity) -> (activity as? FragmentActivity) != null }
            .firstOrError()
            .map { (activity) -> activity as FragmentActivity }
            .flatMapCompletable { activity ->
                Completable.create { requestPermission(activity, request, it) }
            }

    private fun requestPermission(
        activity: FragmentActivity,
        request: PermissionRequest,
        emitter: CompletableEmitter
    ) {
        val requestFragment = PermissionRequestFragment()
        requestFragment.request = request
        requestFragment.resultEmitter = emitter

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