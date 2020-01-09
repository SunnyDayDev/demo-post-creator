package dev.sunnyday.postcreator.domain.backgrounds

import android.net.Uri
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

sealed class Background {

    abstract val id: Long

    data class Color(
        override val id: Long,
        @ColorInt val color: Int
    ) : Background()

    data class Gradient(
        override val id: Long,
        val colors: IntArray
    ) : Background()

    data class Resource(
        override val id: Long,
        @DrawableRes val resId: Int,
        override val icon: Uri? = null
    ) : Background(), HasBackgroundIcon

    data class Stored(
        override val id: Long,
        val uri: Uri,
        override val icon: Uri? = null
    ) : Background(), HasBackgroundIcon

}

interface HasBackgroundIcon {

    val icon: Uri?

}