package dev.sunnyday.postcreator.core.activitytracker

import android.app.Application
import javax.inject.Inject

class ActivityTrackerStarter @Inject internal constructor(
    private val activityTracker: ActivityTracker
) {

    fun start(application: Application) {
        application.registerActivityLifecycleCallbacks(activityTracker)
    }

}