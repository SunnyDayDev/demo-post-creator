package dev.sunnyday.postcreator.core.activitytracker

import android.app.Activity
import dev.sunnyday.postcreator.core.common.util.Optional
import io.reactivex.Observable

interface ActivityTrackerObserver {

    val lastStartedActivity: Observable<Optional<Activity>>

}