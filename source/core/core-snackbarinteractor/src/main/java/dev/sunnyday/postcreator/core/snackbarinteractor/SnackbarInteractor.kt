package dev.sunnyday.postcreator.core.snackbarinteractor

import io.reactivex.Maybe

interface SnackbarInteractor {

    fun showMessageWithAction(message: String, actionText: String): Maybe<Unit>

}