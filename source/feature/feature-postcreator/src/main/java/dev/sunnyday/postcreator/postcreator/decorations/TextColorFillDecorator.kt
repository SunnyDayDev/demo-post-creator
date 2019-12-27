package dev.sunnyday.postcreator.postcreator.decorations

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorInt

class TextColorFillDecorator(@ColorInt private val color: Int) : TextDecorator {

    private val paint = Paint().apply {
        color = this@TextColorFillDecorator.color
    }

    override fun decorateText(canvas: Canvas, lines: List<String>, linesBounds: List<Rect>) {
        linesBounds.forEach {
            canvas.drawRect(it, paint)
        }
    }

}