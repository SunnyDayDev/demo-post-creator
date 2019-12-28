package dev.sunnyday.postcreator

import dev.sunnyday.postcreator.postcreator.decorations.TextDecorator

data class DecoratedTextStyle(
    val textColor: Int,
    val decorations: List<TextDecorator>)