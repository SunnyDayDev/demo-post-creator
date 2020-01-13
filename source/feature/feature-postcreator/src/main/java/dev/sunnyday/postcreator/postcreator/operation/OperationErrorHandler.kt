package dev.sunnyday.postcreator.postcreator.operation

import android.content.Context
import dev.sunnyday.postcreator.core.dialoginteractor.DialogInteractor
import dev.sunnyday.postcreator.core.permissions.PermissionsNotGrantedError
import dev.sunnyday.postcreator.postcreator.R
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

internal class OperationErrorHandler @Inject constructor(
    private val context: Context,
    private val dialogInteractor: DialogInteractor
) {

    fun handle(error: Throwable): Completable =
        if (error is PermissionsNotGrantedError) {
            dialogInteractor.showMessage(
                context.getString(R.string.postcreator__prompt__permission_not_granted_error))
        } else {
            Completable.fromAction {
                Timber.e(error)
            }
        }

}