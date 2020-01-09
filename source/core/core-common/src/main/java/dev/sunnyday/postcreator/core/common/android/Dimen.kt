package dev.sunnyday.postcreator.core.common.android

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

object Dimen {

    fun dp(value: Number, displayMetrics: DisplayMetrics): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), displayMetrics)

    @Suppress("NOTHING_TO_INLINE")
    inline fun dp(value: Number, context: Context): Float =
        dp(value, context.resources.displayMetrics)

}