package dev.sunnyday.postcreator.core.permissions

import android.app.Application
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionRequestInteractorStarter @Inject internal constructor(
    private val callbacks: PermissionRequestInteractorActivityTracker
) {

    fun start(application: Application) {
        application.registerActivityLifecycleCallbacks(callbacks)
    }

}