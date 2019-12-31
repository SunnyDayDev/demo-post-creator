package dev.sunnyday.postcreator.core.ui

import android.app.Activity
import dev.sunnyday.postcreator.core.common.util.Optional
import io.reactivex.Observable

interface ActivityObserver {

    val lastStartedActivity: Observable<Optional<Activity>>

}