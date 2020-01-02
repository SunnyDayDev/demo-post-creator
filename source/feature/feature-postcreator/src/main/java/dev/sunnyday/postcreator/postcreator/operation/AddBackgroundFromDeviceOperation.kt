package dev.sunnyday.postcreator.postcreator.operation

import io.reactivex.Completable

interface AddBackgroundFromDeviceOperation {

    fun execute(): Completable

}