package dev.sunnyday.postcreator.core.activityforresult

import android.content.Context
import android.content.Intent

abstract class ActivityForResultRequest<T: Any>(
    open val code: Int
) {

    abstract fun createIntent(context: Context): Intent

    abstract fun mapResult(resultCode: Int, data: Intent?): T?

}
