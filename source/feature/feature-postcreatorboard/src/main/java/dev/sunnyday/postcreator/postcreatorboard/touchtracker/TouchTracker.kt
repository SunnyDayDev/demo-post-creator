package dev.sunnyday.postcreator.postcreatorboard.touchtracker

import android.view.MotionEvent

internal interface TouchTracker {

    fun onTouchEvent(event: MotionEvent): Boolean

}