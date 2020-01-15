package dev.sunnyday.postcreator.postcreator.operation

import android.graphics.Rect
import dev.sunnyday.postcreator.stickersboard.StickerBoardItem
import io.reactivex.Maybe

interface ChooseStickerOperation {

    fun chooseSticker(stickerRectProvider: () -> Rect): Maybe<StickerBoardItem>

}