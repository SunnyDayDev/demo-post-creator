package dev.sunnyday.postcreator.core.common.android

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

object InputStreamUtil {

    fun inputStreamFromUri(uri: Uri, context: Context): InputStream? = when (uri.scheme) {
        ContentResolver.SCHEME_FILE -> {
            if (uri.pathSegments.firstOrNull() == "android_asset") {
                assetsInputStream(uri, context)
            } else {
                fileInputStream(uri)
            }
        }
        ContentResolver.SCHEME_ANDROID_RESOURCE,
        ContentResolver.SCHEME_CONTENT -> contentInputStream(uri, context)
        else -> null
    }

    private fun assetsInputStream(uri: Uri, context: Context): InputStream? {
        val path = uri.pathSegments
            .drop(1)
            .joinToString("/")

        return context.assets.open(path)
    }

    private fun fileInputStream(uri: Uri): InputStream? =
        uri.path?.let(::FileInputStream)

    private fun contentInputStream(uri: Uri, context: Context): InputStream? =
        context.contentResolver.openInputStream(uri)

}