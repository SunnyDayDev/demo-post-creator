package dev.sunnyday.postcreator.core.permissions

import io.reactivex.Completable

interface PermissionRequestInteractor {

    fun requirePermission(request: PermissionRequest): Completable

}