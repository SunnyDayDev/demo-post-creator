package dev.sunnyday.postcreator.postcreator.operation

import android.graphics.Bitmap
import io.reactivex.Completable

internal interface SavePostOperation {

    fun savePostImage(bitmap: Bitmap): Completable

}