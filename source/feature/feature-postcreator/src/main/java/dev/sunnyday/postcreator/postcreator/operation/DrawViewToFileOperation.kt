package dev.sunnyday.postcreator.postcreator.operation

import android.view.View
import io.reactivex.Completable

internal interface DrawViewToFileOperation {

    fun drawToFile(view: View): Completable

}