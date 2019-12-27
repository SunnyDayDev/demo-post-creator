package dev.sunnyday.postcreator.postcreator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.postDelayed
import androidx.core.widget.addTextChangedListener
import dev.sunnyday.postcreator.postcreator.decorations.TextDecorator
import kotlinx.android.synthetic.main.postcreator__view.view.*

class PostCreatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?  = null, defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val textDecorator = TextDecoratorView(context)

    init {
        addView(textDecorator)

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.postcreator__view, this, true)

        textInput.addTextChangedListener {
            // TODO: https://github.com/SunnyDayDev/demo-post-creator/projects/1#card-31003227
            postDelayed(100, this::decorateText)
        }
    }

    fun addTextDecorator(decorator: TextDecorator) {
        textDecorator.decorators.add(decorator)
    }

    fun removeTextDecorator(decorator: TextDecorator) {
        textDecorator.decorators.remove(decorator)
    }

    // TODO: https://github.com/SunnyDayDev/demo-post-creator/projects/1#card-31003220
    private fun decorateText() {
        val layout = textInput.layout
        val text = textInput.text.toString()

        val lines = mutableListOf<String>()
        val linesBounds = mutableListOf<Rect>()

        Log.d(">>>","Text: $text")

        for (i in 0 until layout.lineCount) {
            val lineStart = layout.getLineStart(i)
            val lineEnd = layout.getLineEnd(i)

            val line = text.substring(lineStart, lineEnd)

            if (line.isBlank()) continue

            lines.add(line)

            val lineWidth = layout.paint.measureText(line).toInt()
            val widthDiff = (layout.width - lineWidth) / 2
            val bounds = Rect()
            layout.getLineBounds(i, bounds)
            bounds.offset(textInput.left, textInput.top)
            bounds.inset(widthDiff, 0)

            linesBounds.add(bounds)
        }

        textDecorator.lines = lines
        textDecorator.linesBounds = linesBounds

        textDecorator.invalidate()
    }

    internal class TextDecoratorView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet?  = null, defStyle: Int = 0
    ) : View(context, attrs, defStyle) {

        var lines: List<String> = emptyList()
        var linesBounds: List<Rect> = emptyList()

        val decorators = mutableSetOf<TextDecorator>()

        override fun draw(canvas: Canvas) {
            super.draw(canvas)
            decorators.forEach {
                it.decorateText(canvas, lines, linesBounds)
            }
        }

    }

}