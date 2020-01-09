package dev.sunnyday.postcreator.postcreator.styles

import dev.sunnyday.postcreator.postcreatorboard.PostCreatorBoardView
import javax.inject.Inject

internal class TextStyleSwitcher @Inject constructor(
    private val stylesSource: TextStylesSource
) {

    var activeTextStyleIndex = 0

    fun applyNextStyle(creatorView: PostCreatorBoardView) {
        activeTextStyleIndex = (activeTextStyleIndex + 1) % stylesSource.getStylesCount()
        applyStyle(creatorView)
    }

    fun applyStyle(creatorView: PostCreatorBoardView) {
        val activeStyle = stylesSource.getStyle(activeTextStyleIndex)
            ?: return

        creatorView.textColor = activeStyle.textColor
        creatorView.setTextDecorators(activeStyle.decorations)
    }

}