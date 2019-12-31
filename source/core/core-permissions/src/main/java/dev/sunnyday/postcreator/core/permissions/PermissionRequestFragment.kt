package dev.sunnyday.postcreator.core.permissions

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import io.reactivex.CompletableEmitter

internal class PermissionRequestFragment : Fragment() {

    var request: PermissionRequest? = null
    var resultEmitter: CompletableEmitter? = null

    private var isDismissed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val request = request
        val resultEmitter = resultEmitter

        if (request == null || resultEmitter == null) {
            dismiss()
            return
        }

        retainInstance = true
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

        if (!isDismissed) {
            resultEmitter?.tryOnError(PermissionsRequestInterruptedError())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        val request = request
        val resultEmitter = resultEmitter

        if (request == null || resultEmitter == null) {
            dismiss()
            return
        }

        if (requestCode == request.code) {

            val nonGrantedPermissions = grantResults
                .withIndex()
                .filter { (_, result) -> result != PackageManager.PERMISSION_GRANTED }
                .map { (i, _) -> permissions[i] }

            if (nonGrantedPermissions.isEmpty()) {
                resultEmitter.onComplete()
            } else {
                resultEmitter.onError(PermissionsNotGrantedError(nonGrantedPermissions))
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

}