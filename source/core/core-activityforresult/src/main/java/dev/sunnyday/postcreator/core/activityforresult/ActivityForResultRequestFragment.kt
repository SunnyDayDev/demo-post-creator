package dev.sunnyday.postcreator.core.activityforresult

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

internal class ActivityForResultRequestFragment<T: Any> private constructor(
    private val executingRequest: ExecutingRequest<T>?
) : Fragment() {

    constructor(): this(null)

    private var isDismissed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = context ?: return

        if (executingRequest == null) {
            dismiss()
            return
        }

        retainInstance = true

        val request = executingRequest.request
        val intent = request.createIntent(context)
        startActivityForResult(intent, request.code)
    }

    override fun onResume() {
        super.onResume()

        if (isDismissed) {
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!isDismissed && executingRequest != null) {
            executingRequest.onError(ActivityForResultRequestInterruptedError())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (executingRequest == null) {
            dismiss()
            return
        }

        if (requestCode == executingRequest.request.code) {
            executingRequest.onResult(executingRequest.request.mapResult(resultCode, data))
        }
    }

    fun dismiss() {
        isDismissed = true

        parentFragmentManager.commit(allowStateLoss = true) {
            remove(this@ActivityForResultRequestFragment)
        }
    }

    private data class ExecutingRequest<T: Any>(
        val request: ActivityRequest<T>,
        val onResult: (T?) -> Unit,
        val onError: (Error) -> Unit)

    companion object {

        fun <T: Any> create(request: ActivityRequest<T>,
                            onResult: (T?) -> Unit,
                            onError: (Error) -> Unit): ActivityForResultRequestFragment<T> {
            val executingRequest = ExecutingRequest(request, onResult, onError)
            return ActivityForResultRequestFragment(executingRequest)
        }

    }

}