package dev.sunnyday.postcreator.postcreator.operation

import android.graphics.Rect
import dev.sunnyday.postcreator.core.dialoginteractor.DialogInteractorManager
import dev.sunnyday.postcreator.domain.stickers.Sticker
import dev.sunnyday.postcreator.domain.stickers.StickersRepository
import dev.sunnyday.postcreator.stickersboard.StickerBoardItem
import dev.sunnyday.postcreator.stickersboard.StickersBoard
import io.reactivex.Maybe
import javax.inject.Inject

internal class ChooseStickerOperationImpl @Inject constructor(
    private val stickersRepository: StickersRepository,
    private val dialogManager: DialogInteractorManager
) : ChooseStickerOperation {

    override fun chooseSticker(
        stickerRectProvider: () -> Rect
    ): Maybe<StickerBoardItem> = stickersRepository
        .stickers()
        .flatMapMaybe { chooseSticker(it, stickerRectProvider) }

    private fun chooseSticker(
        stickers: List<Sticker>,
        stickerRectProvider: () -> Rect
    ): Maybe<StickerBoardItem> = dialogManager
        .showDialog { activity, emitter ->
            val stickerBoardItems = stickers.map { StickerBoardItem(it.sourceUri) }

            StickersBoard.show(
                activity, stickerBoardItems,
                stickerRectProvider = stickerRectProvider,
                callback = emitter::onSuccess)
        }

}