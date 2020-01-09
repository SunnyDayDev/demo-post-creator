package dev.sunnyday.postcreator.postcreator.operation

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.view.drawToBitmap
import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.core.app.permissions.AppPermissionRequest
import dev.sunnyday.postcreator.core.permissions.PermissionRequestInteractor
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

internal class DrawViewToFileOperationImpl @Inject constructor(
    private val context: Context,
    private val permissionsInteractor: PermissionRequestInteractor,
    private val schedulers: AppSchedulers
) : DrawViewToFileOperation {

    override fun drawToFile(view: View): Completable =
        permissionsInteractor.requirePermission(AppPermissionRequest.Storage)
            .andThen(drawViewToBitmap(view))
            .observeOn(schedulers.background)
            .flatMapCompletable(this::save)

    private fun drawViewToBitmap(view: View): Single<Bitmap> = Single.fromCallable {
        view.drawToBitmap(Bitmap.Config.ARGB_8888)
    }

    private fun save(bitmap: Bitmap): Completable = Completable.fromAction {
        val time = System.currentTimeMillis()
        val name = "Post_${time}.jpg"

        val fos: OutputStream
        var postAction: (() -> Unit)? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/")
                put(MediaStore.MediaColumns.DATE_TAKEN, time / 1000)
                put(MediaStore.MediaColumns.DATE_ADDED, time / 1000)
            }
            val imageUri = resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Error("Can't write to file.")

            fos = resolver.openOutputStream(imageUri) ?: throw Error("Can't write to file.")
        } else @Suppress("DEPRECATION") {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(dir, name)
            fos = FileOutputStream(file)

            postAction = {
                MediaScannerConnection
                    .scanFile(context, arrayOf(file.toString()), arrayOf("image/jpeg")) { _, _ -> }
            }
        }

        fos.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)
        }

        postAction?.invoke()
    }

}