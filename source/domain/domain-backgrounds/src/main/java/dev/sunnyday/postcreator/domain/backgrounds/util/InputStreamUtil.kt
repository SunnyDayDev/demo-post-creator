package dev.sunnyday.postcreator.domain.backgrounds.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.FileInputStream
import java.io.InputStream

internal object InputStreamUtil {

    fun inputStreamFromUri(uri: Uri, context: Context): InputStream? = when (uri.scheme) {
        "file" -> uri.path?.let(::FileInputStream)
        ContentResolver.SCHEME_ANDROID_RESOURCE, "content" ->
            context.contentResolver.openInputStream(uri)
        else -> null
    }

}