package dev.sunnyday.postcreator.backgroundswitcher

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

sealed class Background {

    data class Color(@ColorInt val color: Int) : Background()

    data class Gradient(@ColorInt val startColor: Int, @ColorInt val endColor: Int) : Background()

    data class Resource(@DrawableRes val id: Int) : Background()

    companion object {

        fun getDrawable(context: Context, background: Background): Drawable? = when (background) {
            is Color -> ColorDrawable(background.color)
            is Gradient -> {
                val colors = intArrayOf(background.startColor, background.endColor)
                GradientDrawable(GradientDrawable.Orientation.TL_BR, colors)
            }
            is Resource -> ContextCompat.getDrawable(context, background.id)
        }

    }

}