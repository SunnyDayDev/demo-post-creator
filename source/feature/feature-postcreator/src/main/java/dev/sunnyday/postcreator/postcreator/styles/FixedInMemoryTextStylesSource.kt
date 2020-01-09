package dev.sunnyday.postcreator.postcreator.styles

import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import androidx.annotation.ColorInt
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.core.common.android.compat
import dev.sunnyday.postcreator.postcreator.R
import dev.sunnyday.postcreator.postcreatorboard.decorations.RoundedColorFillDecorator
import dev.sunnyday.postcreator.postcreatorboard.decorations.TextDecorator
import javax.inject.Inject

internal class FixedInMemoryTextStylesSource @Inject constructor(
    private val context: Context
) : TextStylesSource {

    private val textStyles = listOf(
        DecoratedTextStyle(
            textColor = context.compat.getColor(R.color.textDark),
            decorations = emptyList()
        ),
        DecoratedTextStyle(
            textColor = context.compat.getColor(R.color.textDark),
            decorations = listOf(roundedWhiteTextDecorator())
        ),
        DecoratedTextStyle(
            textColor = context.compat.getColor(R.color.textLight),
            decorations = listOf(roundedSemiWhiteTextDecorator())
        )
    )

    override fun getStyle(index: Int): DecoratedTextStyle? = textStyles.getOrNull(index)

    override fun getStylesCount(): Int = textStyles.size

    private fun roundedWhiteTextDecorator() =
        roundedCornerFillTextStyle(Color.WHITE)

    private fun roundedSemiWhiteTextDecorator(): TextDecorator {
        val color = context.compat.getColor(R.color.textDecorationSemiWhite)
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

}