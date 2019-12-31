package dev.sunnyday.postcreator.postcreator.saver

import android.view.View
import io.reactivex.Completable

internal interface ViewAsImageSaver {

    fun save(view: View): Completable

}