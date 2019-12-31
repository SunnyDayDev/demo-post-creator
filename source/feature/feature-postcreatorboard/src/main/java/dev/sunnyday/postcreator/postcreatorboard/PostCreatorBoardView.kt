package dev.sunnyday.postcreator.postcreatorboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.graphics.toPoint
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.postcreatorboard.decorations.TextDecorator
import kotlinx.android.synthetic.main.postcreatorboard__view.view.*
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt


class PostCreatorBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?  = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    @get:ColorInt
    var textColor: Int
        get() = textInput.currentTextColor
        set(@ColorInt value) { textInput.setTextColor(value) }

    private var actionsColors: ColorStateList = ColorStateList.valueOf(Color.BLACK)
    private var actionsBorderWidth: Int = Dimen.dp(2, context).toInt()

    private val images = mutableListOf<ImageView>()

    private var activeImageTouchTracker: ImageTouchTracker? = null

    private val textDecorator = TextDecoratorView(context)

    private var deleteButtonCenterPoint: Point = Point(0, 0)
    private val deleteActionRadius = Dimen.dp(36, context)
    private var isDeleteActionActive = false

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        textInput.isEnabled = enabled
    }

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.postcreatorboard__view, this, true)
        addView(textDecorator, 0)

        deleteButton.isVisible = false

        attrs?.let(this::applyAttributes)
        initTextDecorationUpdating()
    }

    private fun applyAttributes(attrs: AttributeSet) {
        val attributes: TypedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.PostCreatorBoardView, 0, 0)

        try {
            attributes.getColorStateList(R.styleable.PostCreatorBoardView_actionsColor)
                ?.let(this::setActionsColor)

            attributes.getDimensionPixelSize(R.styleable.PostCreatorBoardView_actionsBorderWidth, -1)
                .takeIf { it != -1 }
                ?.let(this::setActionsBorderWidth)
        } finally {
            attributes.recycle()
        }
    }

    private fun initTextDecorationUpdating() {
        textInput.addTextChangedListener {
            // TODO: https://github.com/SunnyDayDev/demo-post-creator/projects/1#card-31003227
            postDelayed(10, this::decorateText)
        }
    }

    fun setActionsColor(colors: ColorStateList) {
        actionsColors = colors

        deleteButton.imageTintList = colors
        val background = (deleteButton.background as GradientDrawable)
        background.setStroke(actionsBorderWidth, colors)
    }

    fun setActionsBorderWidth(@Dimension width: Int) {
        actionsBorderWidth = width

        val background = (deleteButton.background as GradientDrawable)
        background.setStroke(width, actionsColors)
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
        images.add(0, image)
        addView(image, indexOfChild(textDecorator))

        val size = Dimen.dp(92, context).toInt()
        image.layoutParams = LayoutParams(size, size)

        Glide.with(context)
            .load(uri)
            .into(image)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        deleteButtonCenterPoint.set(
            deleteButton.left + deleteButton.width / 2,
            deleteButton.top + deleteButton.height / 2)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchTracker = activeImageTouchTracker ?: findTouchTracker(event)
            ?.also(this::activeImageTouchTracker::set)

        return touchTracker?.onTouchEvent(event) ?: super.onTouchEvent(event)
    }

    private fun findTouchTracker(event: MotionEvent): ImageTouchTracker? {
        val image = findImageUnderTouch(event) ?: return null
        return ImageTouchTracker(image, this::onImageMoved, this::onImageInteractionCompleted)
    }

    private fun onImageMoved(touchPoint: Point, image: ImageView) {
        setDeleteActiveActive(isDeleteAction(touchPoint))

        deleteButton.isVisible = true
    }

    private fun isDeleteAction(touchPoint: Point): Boolean {
        val distanceToDelete = getDistanceBetweenPoints(deleteButtonCenterPoint, touchPoint)
        return distanceToDelete <= deleteActionRadius
    }

    private fun setDeleteActiveActive(isActive: Boolean) {
        if (isDeleteActionActive == isActive) return
        isDeleteActionActive = isActive

        if (isActive) {
            val size =  Dimen.dp(56, context).toInt()
            deleteButton.setImageResource(R.drawable.postcreator__ic__fab_trash_released)
            updateDeleteButtonSize(size)
        } else {
            val size =  Dimen.dp(48, context).toInt()
            deleteButton.setImageResource(R.drawable.postcreator__ic__fab_trash)
            updateDeleteButtonSize(size)
        }
    }

    private fun updateDeleteButtonSize(size: Int) {
        if (deleteButton.width == size) return

        deleteButton.updateLayoutParams<LayoutParams> {
            width = size
            height = size
            bottomMargin = this@PostCreatorBoardView.height - deleteButtonCenterPoint.y - size / 2
        }
    }

    private fun onImageInteractionCompleted(touchPoint: Point, image: ImageView) {
        activeImageTouchTracker = null
        deleteButton.isVisible = false

        if (isDeleteAction(touchPoint)) {
            images.remove(image)
            removeView(image)
        }
    }

    private fun findImageUnderTouch(event: MotionEvent): ImageView? {
        val rect = Rect()
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()

        return images.firstOrNull {
            it.getGlobalVisibleRect(rect)
            rect.contains(x, y)
        }
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

    private class ImageTouchTracker(
        private val image: ImageView,
        onMoved: (Point, ImageView) -> Unit,
        onComplete: (Point, ImageView) -> Unit
    ) {

        private var anchorPoint: Point? = null
        private var anchorSize: Int? = null
        private var anchorAngle: Float? = null

        private var firstPointerIndex: Int? = null
        private var firstStartPoint: PointF? = null
        private var firstActualPoint: PointF? = null

        private var secondPointerIndex: Int? = null
        private var secondTouchPoint: PointF? = null
        private var secondActualPoint: PointF? = null

        private var startSize: Int? = null
        private var startDistance: Float? = null
        private var startAngle: Float? = null

        private val onMovedCallback = onMoved
        private val onCompleteCallback = onComplete

        fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    firstPointerIndex = event.actionIndex

                    val touchPoint = getTouchPoint(event.actionIndex, event)
                    firstStartPoint = touchPoint
                    firstActualPoint = touchPoint

                    anchorSize = image.width
                    anchorPoint = (image.layoutParams as LayoutParams).let {
                        Point(it.leftMargin, it.topMargin)
                    }
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    if(secondPointerIndex == null) {
                        secondPointerIndex = event.actionIndex

                        val touchPoint = getTouchPoint(event.actionIndex, event)
                        secondTouchPoint = touchPoint
                        secondActualPoint = touchPoint

                        startSize = image.width

                        val firstTouchPoint = firstActualPoint ?: return true
                        startDistance = getDistanceBetweenPoints(firstTouchPoint, touchPoint)
                        startAngle = getAngleBetweenPoints(firstTouchPoint, touchPoint)
                        anchorAngle = image.rotation
                    } else {
                        return true
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    val firstPointerIndex = firstPointerIndex
                    if (firstPointerIndex != null) {
                        firstActualPoint = getTouchPoint(firstPointerIndex, event)
                    }

                    val secondPointerIndex = secondPointerIndex
                    if (secondPointerIndex != null) {
                        secondActualPoint = getTouchPoint(secondPointerIndex, event)
                    }

                    onMoved()
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    if (firstActualPoint != null) {
                        onCompleteCallback(firstActualPoint!!.toPoint(), image)
                    }

                    firstPointerIndex = null
                    firstStartPoint = null
                    firstActualPoint = null
                    secondPointerIndex = null
                    secondTouchPoint = null
                    secondActualPoint = null
                    startDistance = null
                    return false
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    secondPointerIndex = null
                    secondTouchPoint = null
                    secondActualPoint = null
                    startDistance = null
                }
            }

            return true
        }

        private fun onMoved() {
            val anchorPoint = anchorPoint ?:return
            val firstActualPoint = firstActualPoint ?:return
            val firstStartPoint = firstStartPoint ?:return
            val anchorSize = anchorSize ?: return

            var size = image.width

            val secondActualPoint = secondActualPoint

            if (secondActualPoint != null) {

                val startDistance = startDistance
                val startSize = startSize

                if (startSize != null && startDistance != null) {
                    val actualDistance = getDistanceBetweenPoints(firstActualPoint, secondActualPoint)
                    size = (startSize * actualDistance / startDistance).toInt()
                }

                val startAngle = startAngle
                val anchorAngle = anchorAngle
                if (startAngle != null && anchorAngle != null) {
                    val angle = getAngleBetweenPoints(firstActualPoint, secondActualPoint)
                    image.rotation = anchorAngle + angle - startAngle
                }

            }

            val sizeShift = (size - anchorSize) / 2

            image.updateLayoutParams<LayoutParams> {
                width = size
                height = size

                setMargins(
                    anchorPoint.x + (firstActualPoint.x - firstStartPoint.x).toInt() - sizeShift,
                    anchorPoint.y + (firstActualPoint.y - firstStartPoint.y).toInt() - sizeShift,
                    0, 0)

            }

            onMovedCallback(firstActualPoint.toPoint(), image)
        }

        private fun getTouchPoint(index: Int, event: MotionEvent): PointF =
            PointF(
                event.getX(index),
                event.getY(index))

        private fun getAngleBetweenPoints(f: PointF, s: PointF): Float {
            val atan = atan2((f.y - s.y).toDouble(), (f.x - s.x).toDouble())
            val angle = Math.toDegrees(atan).toFloat()

            return if (angle < 0) {
                angle + 360f
            } else {
                angle
            }
        }

    }

    companion object {

        private fun getDistanceBetweenPoints(f: PointF, s: PointF): Float =
            sqrt((s.x - f.x).pow(2) + (s.y - f.y).pow(2))

        private fun getDistanceBetweenPoints(f: Point, s: Point): Float =
            sqrt((s.x - f.x).toFloat().pow(2) + (s.y - f.y).toFloat().pow(2))

    }

}