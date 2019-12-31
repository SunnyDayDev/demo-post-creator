package dev.sunnyday.postcreator.postcreator

import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.postcreatorboard.PostCreatorBoardView
import dev.sunnyday.postcreator.postcreatorboard.decorations.RoundedColorFillDecorator
import dev.sunnyday.postcreator.postcreatorboard.decorations.TextDecorator

internal class TextStyleSwitcher(private val context: Context) {

    private val textStyles: List<DecoratedTextStyle>
    private var activeTextStyleIndex = -1

    init {
        textStyles = listOf(
            DecoratedTextStyle(
                textColor = Color.BLACK,
                decorations = emptyList()
            ),
            DecoratedTextStyle(
                textColor = Color.BLACK,
                decorations = listOf(roundedWhiteTextDecorator())
            ),
            DecoratedTextStyle(
                textColor = Color.WHITE,
                decorations = listOf(roundedSemiWhiteTextDecorator())
            )
        )
    }

    private fun roundedWhiteTextDecorator() =
        roundedCornerFillTextStyle(Color.WHITE)

    private fun roundedSemiWhiteTextDecorator(): TextDecorator {
        val color = ContextCompat.getColor(context, R.color.textDecorationSemiWhite)
        return roundedCornerFillTextStyle(color)
    }

    private fun roundedCornerFillTextStyle(@ColorInt color: Int): TextDecorator {
        val decoratorPadding = RectF(
            Dimen.dp(4, context),
            Dimen.dp(8, context),
            Dimen.dp(4, context),
            Dimen.dp(0, context))

        return RoundedColorFillDecorator(
            color = color,
            radius = Dimen.dp(4, context),
            padding = decoratorPadding)
    }

    fun applyNextStyle(creatorView: PostCreatorBoardView) {
        activeTextStyleIndex = (activeTextStyleIndex + 1) % textStyles.size
        val activeStyle = textStyles.getOrNull(activeTextStyleIndex) ?: return
        creatorView.textColor = activeStyle.textColor
        creatorView.setTextDecorators(activeStyle.decorations)
    }

}