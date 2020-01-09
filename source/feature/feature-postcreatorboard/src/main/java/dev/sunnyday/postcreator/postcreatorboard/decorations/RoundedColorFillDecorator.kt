package dev.sunnyday.postcreator.postcreatorboard.decorations

import android.graphics.*
import androidx.annotation.ColorInt

class RoundedColorFillDecorator(
    @ColorInt private val color: Int,
    private val radius: Float,
    private val padding: RectF = RectF()
) : TextDecorator {

    private val paint = Paint().apply {
        color = this@RoundedColorFillDecorator.color
        pathEffect = CornerPathEffect(radius)
    }

    override fun decorateText(canvas: Canvas, lines: List<TextDecorator.Line>) {
        val summaryPath = Path()
        val path = Path()

        lines.forEach {
            path.reset()
            path.addRect(
                it.bounds.left.toFloat() - padding.left,
                it.bounds.top.toFloat() - padding.top,
                it.bounds.right.toFloat() + padding.right,
                it.bounds.bottom.toFloat() + padding.bottom,
                Path.Direction.CW)

            summaryPath.op(path, Path.Op.UNION)
        }

        canvas.drawPath(summaryPath, paint)
    }

}