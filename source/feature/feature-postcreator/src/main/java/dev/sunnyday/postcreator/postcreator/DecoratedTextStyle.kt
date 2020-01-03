package dev.sunnyday.postcreator.postcreator

import dev.sunnyday.postcreator.postcreatorboard.decorations.TextDecorator

internal data class DecoratedTextStyle(
    val id: Long,
    val textColor: Int,
    val decorations: List<TextDecorator>)