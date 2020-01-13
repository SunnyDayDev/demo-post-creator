package dev.sunnyday.postcreator.postcreatorboard

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import dev.sunnyday.postcreator.postcreatorboard.decorations.TextDecorator
import kotlin.math.max

internal class DecoratableEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?  = null, defStyle: Int = R.attr.editTextStyle
): AppCompatEditText(context, attrs, defStyle) {

    private var lines: List<TextDecorator.Line> = emptyList()

    private val decorators = mutableSetOf<TextDecorator>()
    private var decoration: Bitmap? = null

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            invalidateDecorationTextLines()
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        invalidateDecorationTextLines()
    }

    override fun onDraw(canvas: Canvas) {
        drawDecoration(canvas)
        super.onDraw(canvas)
    }

    private fun drawDecoration(canvas: Canvas) {
        val decoration = decoration
            ?: return

        canvas.drawBitmap(decoration, 0f, 0f, null)
    }

    fun setTextDecorators(decorators: List<TextDecorator>) {
        this.decorators.clear()
        this.decorators.addAll(decorators)

        invalidateDecoration()
    }

    private fun invalidateDecoration() {
        if (lines.isEmpty()) {
            removeTextDecoration()
        } else {
            updateTextDecoration()
        }

        invalidate()
    }

    private fun updateTextDecoration() {
        val decorationHeight = max(layout.height, height)

        val bitmap = decoration?.takeIf { it.width == width && it.height == decorationHeight }
            ?: Bitmap.createBitmap(width, decorationHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        decorators.forEach {
            it.decorateText(canvas, lines)
        }

        if (bitmap != decoration) {
            decoration?.recycle()
            decoration = bitmap
        }
    }

    private fun removeTextDecoration() {
        decoration?.recycle()
        decoration = null
    }

    private fun invalidateDecorationTextLines() {
        val layout = layout ?: return
        val text = text ?: return

        val lines = mutableListOf<TextDecorator.Line>()

        for (i in 0 until layout.lineCount) {
            val lineStart = layout.getLineStart(i)
            val lineEnd = layout.getLineEnd(i).let {
                if (it > lineStart && text[it - 1] == '\n') it - 1 else it
            }

            val lineText = text.substring(lineStart, lineEnd)

            if (lineText.isEmpty()) continue

            val bounds = Rect()
            layout.getLineBounds(i, bounds)

            val verticalOffset = if (layout.height < height) (height - layout.height) / 2 else 0
            bounds.offset(paddingLeft, paddingTop + verticalOffset)

            val lineWidth = layout.paint.measureText(lineText).toInt()
            val lineInset = (layout.width - lineWidth) / 2
            bounds.inset(lineInset, 0)

            lines.add(TextDecorator.Line(lineText, bounds))
        }

        this.lines = lines
        invalidateDecoration()
    }

}