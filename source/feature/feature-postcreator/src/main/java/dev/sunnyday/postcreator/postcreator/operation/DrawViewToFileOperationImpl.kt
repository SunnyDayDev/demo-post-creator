package dev.sunnyday.postcreator.postcreator.operation

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import dev.sunnyday.postcreator.core.activityforresult.ActivityRequestInteractor
import dev.sunnyday.postcreator.core.app.activityreqest.AppActivityRequests
import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.core.app.permissions.AppPermissionRequest
import dev.sunnyday.postcreator.core.permissions.PermissionRequestInteractor
import dev.sunnyday.postcreator.core.snackbarinteractor.SnackbarInteractor
import dev.sunnyday.postcreator.postcreator.BuildConfig
import dev.sunnyday.postcreator.postcreator.R
import dev.sunnyday.postcreator.postcreator.provider.PostShareProvider
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

internal class DrawViewToFileOperationImpl @Inject constructor(
    private val context: Context,
    private val permissionsInteractor: PermissionRequestInteractor,
    private val snackbarInteractor: SnackbarInteractor,
    private val activityInteractor: ActivityRequestInteractor,
    private val schedulers: AppSchedulers
) : DrawViewToFileOperation {

    override fun drawToFile(view: View): Completable =
        permissionsInteractor.requirePermission(AppPermissionRequest.Storage)
            .andThen(drawViewToBitmap(view))
            .observeOn(schedulers.background)
            .flatMap(this::save)
            .flatMapCompletable(this::notifySaved)

    private fun drawViewToBitmap(view: View): Single<Bitmap> = Single.fromCallable {
        view.drawToBitmap(Bitmap.Config.ARGB_8888)
    }

    private fun save(bitmap: Bitmap): Single<Uri> = Single.fromCallable {
        val time = System.currentTimeMillis()
        val name = "Post_${time}.jpg"

        val fos: OutputStream
        var postAction: (() -> Unit)? = null

        val resultUri: Uri

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE)
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/")
                put(MediaStore.MediaColumns.DATE_TAKEN, time / 1000)
                put(MediaStore.MediaColumns.DATE_ADDED, time / 1000)
            }
            resultUri = resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Error("Can't write to file.")

            fos = resolver.openOutputStream(resultUri) ?: throw Error("Can't write to file.")
        } else @Suppress("DEPRECATION") {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

            if (!dir.exists() && !dir.mkdirs()) {
                throw Error("Can't create dir: $dir")
            }

            val file = File(dir, name)
            fos = FileOutputStream(file)

            postAction = {
                MediaScannerConnection
                    .scanFile(context, arrayOf(file.toString()), arrayOf(MIME_TYPE)) { _, _ -> }
            }

            resultUri = FileProvider.getUriForFile(
                context, PostShareProvider.AUTHORITY, file)
        }

        fos.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)
        }

        postAction?.invoke()

        resultUri
    }

    private fun notifySaved(result: Uri): Completable =
        snackbarInteractor.showMessageWithAction(
            context.getString(R.string.postcreator__prompt__saved_in_folder, "Pictures"),
            context.getString(R.string.core_app__prompt__share))
            .flatMapCompletable { share(result) }

    private fun share(result: Uri): Completable {
        val shareRequest = AppActivityRequests.Share(result, MIME_TYPE)
        return activityInteractor.startActivity(shareRequest)
    }

    companion object {

        private const val MIME_TYPE = "image/jpeg"

    }

}