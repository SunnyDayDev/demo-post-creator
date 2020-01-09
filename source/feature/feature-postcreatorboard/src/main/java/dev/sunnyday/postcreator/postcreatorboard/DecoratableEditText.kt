package dev.sunnyday.postcreator.postcreatorboard

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
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
            invalidateDecoration()
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        decorateText()
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
            removeDecoration()
        } else {
            createOrUpdateDecoration()
        }

        invalidate()
    }

    private fun createOrUpdateDecoration() {
        val decorationHeight = max(layout.height, height)

        val bitmap = decoration?.takeIf { it.width == width && it.height == decorationHeight }
            ?: Bitmap.createBitmap(width, decorationHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        if (layout.height < height) {
            canvas.translate(0f, (height - layout.height) / 2f)
        }

        decorators.forEach {
            it.decorateText(canvas, lines)
        }

        if (bitmap != decoration) {
            decoration?.recycle()
            decoration = bitmap
        }
    }

    private fun removeDecoration() {
        decoration?.recycle()
        decoration = null
    }

    private fun decorateText() {
        val layout = layout ?: return
        val text = text.toString()

        val lines = mutableListOf<TextDecorator.Line>()

        for (i in 0 until layout.lineCount) {
            val lineStart = layout.getLineStart(i)
            val lineEnd = layout.getLineEnd(i).let {
                if (it > lineStart && text[it - 1] == '\n') it - 1 else it
            }

            val lineText = text.substring(lineStart, lineEnd)

            if (lineText.isEmpty()) continue

            val lineWidth = layout.paint.measureText(lineText).toInt()
            val widthDiff = (layout.width - lineWidth) / 2
            val bounds = Rect()
            layout.getLineBounds(i, bounds)
            bounds.offset(paddingLeft, paddingTop)
            bounds.inset(widthDiff, 0)

            lines.add(TextDecorator.Line(lineText, bounds))
        }

        this.lines = lines
        invalidateDecoration()
    }

}