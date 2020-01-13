package dev.sunnyday.postcreator.core.dialoginteractor

import io.reactivex.Completable

interface DialogInteractor {

    fun showMessage(message: String): Completable

}