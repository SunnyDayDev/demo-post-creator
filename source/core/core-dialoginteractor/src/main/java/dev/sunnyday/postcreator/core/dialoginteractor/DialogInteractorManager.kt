package dev.sunnyday.postcreator.core.dialoginteractor

import android.app.Activity
import android.app.Dialog
import io.reactivex.*

interface DialogInteractorManager {

    fun <T> showDialog(factory: DialogFactory<T>): Maybe<T>

}

typealias DialogFactory<T> = (activity: Activity, emitter: MaybeEmitter<T>) -> Dialog