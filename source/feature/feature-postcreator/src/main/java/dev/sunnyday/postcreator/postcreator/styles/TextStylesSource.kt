package dev.sunnyday.postcreator.postcreator.styles

internal interface TextStylesSource {

    fun getStyle(index: Int): DecoratedTextStyle?

    fun getStylesCount(): Int

}