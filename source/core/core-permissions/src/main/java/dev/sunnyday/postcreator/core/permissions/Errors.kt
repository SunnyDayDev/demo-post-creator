package dev.sunnyday.postcreator.core.permissions

import java.lang.Error

class PermissionsNotGrantedError(private val permissions: List<String>) : Error() {

    override fun toString(): String = "Permissions not granted: ${permissions.joinToString()}"

}

class PermissionsRequestInterruptedError : Error()