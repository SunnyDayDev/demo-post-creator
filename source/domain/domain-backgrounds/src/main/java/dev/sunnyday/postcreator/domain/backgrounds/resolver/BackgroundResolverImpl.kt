package dev.sunnyday.postcreator.domain.backgrounds.resolver

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import androidx.core.content.ContextCompat
import dev.sunnyday.postcreator.domain.backgrounds.Background
import dev.sunnyday.postcreator.domain.backgrounds.HasBackgroundIcon
import dev.sunnyday.postcreator.domain.backgrounds.util.InputStreamUtil
import javax.inject.Inject

internal class BackgroundResolverImpl @Inject constructor(
    private val context: Context
): BackgroundResolver {

    override fun resolveIcon(background: Background): Drawable? = when (background) {
        is HasBackgroundIcon -> {
            val iconUri = background.icon
            if (iconUri != null) {
                resolveDrawableByUri(iconUri)
            } else {
                resolve(background)
            }
        }
        is Background.Color -> {
            if (background.color == Color.WHITE)
                ColorDrawable(0x1E000000)
            else
                resolve(background)
        }
        else -> resolve(background)
    }

    override fun resolve(background: Background): Drawable? = when (background) {
        is Background.Color -> ColorDrawable(background.color)
        is Background.Gradient -> GradientDrawable(GradientDrawable.Orientation.TL_BR, background.colors)
        is Background.Resource -> ContextCompat.getDrawable(context, background.resId)
        is Background.Stored -> resolveDrawableByUri(background.uri)
    }

    private fun resolveDrawableByUri(uri: Uri): Drawable? {
        val inputStream = InputStreamUtil.inputStreamFromUri(uri, context)
            ?: return null

        return Drawable.createFromStream(inputStream, null)
    }

}