package dev.sunnyday.postcreator.postcreator.styles

import dev.sunnyday.postcreator.postcreatorboard.decorations.TextDecorator

internal data class DecoratedTextStyle(
    val textColor: Int,
    val decorators: List<TextDecorator>)