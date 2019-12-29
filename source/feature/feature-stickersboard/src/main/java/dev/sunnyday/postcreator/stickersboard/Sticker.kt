package dev.sunnyday.postcreator.stickersboard

import android.net.Uri

data class Sticker(val id: Long) {

    val uri: Uri get() = Uri.parse("file:///android_asset/stickers/$id.png")

}