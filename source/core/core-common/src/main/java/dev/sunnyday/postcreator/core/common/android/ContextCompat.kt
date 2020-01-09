package dev.sunnyday.postcreator.core.common.android

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

inline class ContextCompatHelper(val context: Context) {

    @ColorInt
    fun getColor(@ColorRes id: Int): Int = ContextCompat.getColor(context, id)

}

inline val Context.compat: ContextCompatHelper get() = ContextCompatHelper(this)