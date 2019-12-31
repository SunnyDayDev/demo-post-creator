package dev.sunnyday.postcreator.postcreatorboard.decorations

import android.graphics.Canvas
import android.graphics.Rect

interface TextDecorator {

    fun decorateText(canvas: Canvas, lines: List<Line>)

    data class Line(val text: String, val bounds: Rect)

}