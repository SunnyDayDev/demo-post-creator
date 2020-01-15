package dev.sunnyday.postcreator.postcreator.viewModel

import android.graphics.Rect
import dev.sunnyday.postcreator.domain.backgrounds.Background
import dev.sunnyday.postcreator.postcreator.styles.DecoratedTextStyle
import dev.sunnyday.postcreator.stickersboard.StickerBoardItem

internal interface PostCreatorViewInterface {

    fun getResultStickerBoardItemRect(): Rect

    fun setSelectedBackground(background: Background)

    fun setAvailableBackgrounds(backgrounds: List<Background>)

    fun onStickerBoardItemSelected(item: StickerBoardItem)

    fun applyTextStyle(style: DecoratedTextStyle)

}