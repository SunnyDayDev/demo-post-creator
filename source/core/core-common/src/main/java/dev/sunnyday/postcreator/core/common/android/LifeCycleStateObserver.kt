package dev.sunnyday.postcreator.core.common.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class LifeCycleStateObserver(
    private val lifecycle: Lifecycle,
    private val scope: Lifecycle.State,
    private val isInStateCallback: (isInState: Boolean) -> Unit
) : LifecycleObserver {

    private var currentIsInState = false

    init {
        onAny()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    internal fun onAny() {
        val isInState = lifecycle.currentState.isAtLeast(scope)
        if (isInState != currentIsInState) {
            currentIsInState = isInState
            isInStateCallback(isInState)
        }
    }

}