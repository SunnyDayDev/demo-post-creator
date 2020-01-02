package dev.sunnyday.postcreator.core.common.android

import android.app.Dialog
import androidx.lifecycle.Lifecycle

fun Dialog.attachToLifecycle(lifecycle: Lifecycle) {
    val dismissObserver = LifeCycleStateObserver(lifecycle, Lifecycle.State.STARTED) {
        if (!it) {
            dismiss()
        }
    }

    lifecycle.addObserver(dismissObserver)

    setOnDismissListener {
        lifecycle.removeObserver(dismissObserver)
    }
}