package dev.sunnyday.postcreator.core.app.activityreqest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import dev.sunnyday.postcreator.core.activityforresult.ActivityForResultRequest

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

}