package dev.sunnyday.postcreator.core.permissions

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

internal class PermissionRequestFragment private constructor(
    private val executingRequest: ExecutingPermissionRequest?
) : Fragment() {

    constructor(): this(null)

    private var isDismissed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (executingRequest == null) {
            dismiss()
            return
        }

        retainInstance = true

        val request = executingRequest.request
        requestPermissions(request.permissions, request.code)
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
            executingRequest.onError(PermissionsRequestInterruptedError())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (executingRequest == null) {
            dismiss()
            return
        }

        val (request, onSuccess, onError) = executingRequest

        if (requestCode == request.code) {

            val nonGrantedPermissions = grantResults
                .withIndex()
                .filter { (_, result) -> result != PackageManager.PERMISSION_GRANTED }
                .map { (i, _) -> permissions[i] }

            if (nonGrantedPermissions.isEmpty()) {
                onSuccess.invoke()
            } else {
                onError.invoke(PermissionsNotGrantedError(nonGrantedPermissions))
            }

            dismiss()
        }
    }

    fun dismiss() {
        isDismissed = true

        parentFragmentManager.commit(allowStateLoss = true) {
            remove(this@PermissionRequestFragment)
        }
    }

    private data class ExecutingPermissionRequest(
        val request: PermissionRequest,
        val onSuccess: () -> Unit,
        val onError: (Error) -> Unit)

    companion object {

        fun create(request: PermissionRequest,
                   onSuccess: () -> Unit,
                   onError: (Error) -> Unit): PermissionRequestFragment {
            val executingRequest = ExecutingPermissionRequest(request, onSuccess, onError)
            return PermissionRequestFragment(executingRequest)
        }

    }

}