package dev.sunnyday.postcreator.core.common.android

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri


object UriUtil {

    fun resourceUri(resId: Int, context: Context): Uri {
        val resources: Resources = context.resources

        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(resId))
            .appendPath(resources.getResourceTypeName(resId))
            .appendPath(resources.getResourceEntryName(resId))
            .build()
    }

}