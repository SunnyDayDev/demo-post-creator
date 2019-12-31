package dev.sunnyday.postcreator.core.permissions

import android.Manifest

sealed class AppPermissionRequest(
    requestCode: Int,
    permissions: Array<String>
) : PermissionRequest(requestCode, permissions) {

    object Storage : AppPermissionRequest(1,
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    ))

}