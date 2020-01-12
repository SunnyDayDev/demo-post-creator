package dev.sunnyday.postcreator.core.activitytracker

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dev.sunnyday.postcreator.core.common.util.Optional
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ActivityTracker @Inject constructor()
    : Application.ActivityLifecycleCallbacks, ActivityTrackerObserver {

    private val startedActivities = BehaviorSubject.createDefault<List<Activity>>(emptyList())

    override val lastStartedActivity: Observable<Optional<Activity>> =
        startedActivities.map { Optional(it.lastOrNull()) }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { }

    override fun onActivityStarted(activity: Activity) {
        val currentlyStarted = startedActivities.value ?: emptyList()
        startedActivities.onNext(currentlyStarted + activity)
    }

    override fun onActivityResumed(activity: Activity) { }

    override fun onActivityPaused(activity: Activity) { }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }

    override fun onActivityStopped(activity: Activity) {
        val currentlyStarted = startedActivities.value ?: emptyList()
        startedActivities.onNext(currentlyStarted - activity)
    }

    override fun onActivityDestroyed(activity: Activity) { }

}