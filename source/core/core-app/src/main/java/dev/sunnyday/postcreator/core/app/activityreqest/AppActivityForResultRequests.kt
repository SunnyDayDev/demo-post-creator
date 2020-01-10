package dev.sunnyday.postcreator.core.app.activityreqest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.theartofdev.edmodo.cropper.CropImageView
import dev.sunnyday.postcreator.core.activityforresult.ActivityForResultRequest
import com.theartofdev.edmodo.cropper.CropImage as ImageCropper


sealed class AppActivityForResultRequests<T: Any>(code: Int) : ActivityForResultRequest<T>(code) {

    class PickImage(
        private val chooserTitle: String? = null
    ) : AppActivityForResultRequests<Uri>(1) {

        override fun createIntent(context: Context): Intent {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }

            return Intent.createChooser(intent, chooserTitle ?: "")
        }

        override fun mapResult(resultCode: Int, data: Intent?): Uri? {
            if (resultCode != Activity.RESULT_OK) return null
            return data?.data
        }

    }

    class CropImage(
        private val imageUri: Uri,
        private val widthToHeightRatio: Float? = null
    ) : AppActivityForResultRequests<Uri>(2) {

        override fun createIntent(context: Context): Intent =
            ImageCropper.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .apply {
                    if (widthToHeightRatio != null) {
                        val ratioX = (10000 * widthToHeightRatio).toInt()
                        val ratioY = 10000
                        setAspectRatio(ratioX, ratioY)
                    }
                }
                .getIntent(context)

        override fun mapResult(resultCode: Int, data: Intent?): Uri? {
            val result: ImageCropper.ActivityResult = ImageCropper.getActivityResult(data)
            return result.uri
        }

    }

}