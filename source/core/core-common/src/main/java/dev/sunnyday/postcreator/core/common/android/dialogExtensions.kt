package dev.sunnyday.postcreator.core.common.android

import android.app.Dialog
import androidx.lifecycle.Lifecycle

fun Dialog.attachToLifecycle(
    lifecycle: Lifecycle,
    onDismiss: ((isManualDismiss: Boolean) -> Unit)? = null
) {
    var isManualDismiss = true

    val dismissObserver = LifeCycleStateObserver(lifecycle, Lifecycle.State.STARTED) { isInState ->
        if (!isInState && this.isShowing) {
            isManualDismiss = false
            dismiss()
        }
    }

    lifecycle.addObserver(dismissObserver)

    setOnDismissListener {
        onDismiss?.invoke(isManualDismiss)
        lifecycle.removeObserver(dismissObserver)
    }
}