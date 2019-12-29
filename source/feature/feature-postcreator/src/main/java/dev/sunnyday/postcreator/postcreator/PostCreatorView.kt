package dev.sunnyday.postcreator.postcreator

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.postDelayed
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.postcreator.decorations.TextDecorator
import kotlinx.android.synthetic.main.postcreator__view.view.*

class PostCreatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?  = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    @get:ColorInt
    var textColor: Int
        get() = textInput.currentTextColor
        set(@ColorInt value) { textInput.setTextColor(value) }

    private val textDecorator = TextDecoratorView(context)

    init {
        addView(textDecorator)

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.postcreator__view, this, true)

        textInput.addTextChangedListener {
            // TODO: https://github.com/SunnyDayDev/demo-post-creator/projects/1#card-31003227
            postDelayed(10, this::decorateText)
        }
    }

    fun setTextDecorators(decorators: List<TextDecorator>) {
        textDecorator.setTextDecorators(decorators)
    }

    fun addTextDecorator(decorator: TextDecorator) {
        textDecorator.addTextDecorator(decorator)
    }

    fun removeTextDecorator(decorator: TextDecorator) {
        textDecorator.removeTextDecorator(decorator)
    }

    fun addImage(uri: Uri) {
        val image = ImageView(context)
        addView(image, childCount - 2)

        val size = Dimen.dp(92, context).toInt()
        image.layoutParams = LayoutParams(size, size)

        Glide.with(context)
            .load(uri)
            .into(image)
    }

    // TODO: https://github.com/SunnyDayDev/demo-post-creator/projects/1#card-31003220
    private fun decorateText() {
        val layout = textInput.layout
        val text = textInput.text.toString()

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
            bounds.offset(textInput.left, textInput.top)
            bounds.inset(widthDiff, 0)

            lines.add(TextDecorator.Line(lineText, bounds))
        }

        textDecorator.decorate(lines)
    }

    private class TextDecoratorView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet?  = null, defStyle: Int = 0
    ) : View(context, attrs, defStyle) {

        private var lines: List<TextDecorator.Line> = emptyList()

        private val decorators = mutableSetOf<TextDecorator>()
        private var decoration: Bitmap? = null

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            super.onLayout(changed, left, top, right, bottom)
            if (changed) {
                invalidateDecoration()
            }
        }

        override fun draw(canvas: Canvas) {
            super.draw(canvas)

            val decoration = this.decoration ?: return
            canvas.drawBitmap(decoration, 0f, 0f, null)
        }

        fun decorate(lines: List<TextDecorator.Line>) {
            this.lines = lines

            invalidateDecoration()
        }

        fun setTextDecorators(decorators: List<TextDecorator>) {
            this.decorators.clear()
            this.decorators.addAll(decorators)
            invalidateDecoration()
        }

        fun addTextDecorator(decorator: TextDecorator) {
            decorators.add(decorator)
            invalidateDecoration()
        }

        fun removeTextDecorator(decorator: TextDecorator) {
            decorators.remove(decorator)
            invalidateDecoration()
        }

        fun invalidateDecoration() {
            if (lines.isEmpty()) {
                removeDecoration()
            } else {
                createOrUpdateDecoration()
            }

            invalidate()
        }

        private fun createOrUpdateDecoration() {
            val bitmap = decoration?.takeIf { it.width == width && it.height == height }
                ?: Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

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

        private fun removeDecoration() {
            decoration?.recycle()
            decoration = null
        }

    }

}