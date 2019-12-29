package dev.sunnyday.postcreator.stickersboard

import android.content.Context
import java.io.InputStream

class StickerResolver(private val context: Context) {

    fun getInputStream(sticker: Sticker): InputStream =
        context.assets.open("${sticker.id}.png")

}