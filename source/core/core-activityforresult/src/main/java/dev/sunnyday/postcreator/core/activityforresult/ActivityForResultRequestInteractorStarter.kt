package dev.sunnyday.postcreator.core.activityforresult

import android.app.Application
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityForResultRequestInteractorStarter @Inject internal constructor(
    private val callbacks: ActivityForResultRequestInteractorActivityTracker
) {

    fun start(application: Application) {
        application.registerActivityLifecycleCallbacks(callbacks)
    }

}