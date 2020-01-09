package dev.sunnyday.postcreator.core.ui

import android.app.Activity
import android.os.Bundle
import dev.sunnyday.postcreator.core.common.util.Optional
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ActivityTrackerImpl @Inject constructor() : ActivityTracker, ActivityObserver {

    private val startedActivities = BehaviorSubject.createDefault<Set<Activity>>(emptySet())

    override val lastStartedActivity: Observable<Optional<Activity>> =
        startedActivities.map { Optional(it.lastOrNull()) }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { }

    override fun onActivityStarted(activity: Activity) {
        val currentlyStarted = startedActivities.value ?: emptySet()
        startedActivities.onNext(currentlyStarted + activity)
    }

    override fun onActivityResumed(activity: Activity) { }

    override fun onActivityPaused(activity: Activity) { }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }

    override fun onActivityStopped(activity: Activity) { }

    override fun onActivityDestroyed(activity: Activity) { }

}