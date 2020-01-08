package dev.sunnyday.postcreator.postcreatorboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.core.common.math.MathUtil
import dev.sunnyday.postcreator.postcreatorboard.decorations.TextDecorator
import dev.sunnyday.postcreator.postcreatorboard.touchtracker.ImageTouchTrackerFactory
import dev.sunnyday.postcreator.postcreatorboard.touchtracker.TouchTracker
import kotlinx.android.synthetic.main.postcreatorboard__view.view.*
import java.util.*


class PostCreatorBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?  = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    @get:ColorInt
    var textColor: Int
        get() = textInput.currentTextColor
        set(@ColorInt value) { textInput.setTextColor(value) }

    @get:ColorInt
    var hintTextColor: Int
        get() = textInput.currentHintTextColor
        set(@ColorInt value) { textInput.setHintTextColor(value) }

    val images: List<PostCreatorImage>
        get() = imagesMap.values.toList()

    var text: String
        get() = textInput.text.toString()
        set(value) { textInput.setText(value) }

    private var actionsColors: ColorStateList = ColorStateList.valueOf(Color.BLACK)
    private var actionsBorderWidth: Int = Dimen.dp(2, context).toInt()

    private val imageViewsMap = mutableMapOf<UUID, ImageView>()
    private val imagesMap = mutableMapOf<UUID, PostCreatorImage>()

    private var activeTouchTracker: TouchTracker? = null
    private val touchTrackerFactory = ImageTouchTrackerFactory()

    private val textDecorator = TextDecoratorView(context)

    private var deleteButtonCenterPoint: PointF = PointF(0f, 0f)
    private val deleteActionRadius = Dimen.dp(36, context)
    private var isDeleteActionActive = false

    private var textChangedListeners = mutableSetOf<TextChangedListener>()

    private var imageStateListeners = mutableSetOf<ImageStateListener>()

    override fun setEnabled(isEnabled: Boolean) {
        super.setEnabled(isEnabled)
        textInput.isEnabled = isEnabled
    }

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.postcreatorboard__view, this, true)
        addView(textDecorator, 0)

        deleteButton.isVisible = false

        attrs?.let(this::applyAttributes)
        initTextChangingTracking()
    }

    private fun applyAttributes(attrs: AttributeSet) {
        val attributes: TypedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.PostCreatorBoardView, 0, 0)

        try {
            if (attributes.hasValue(R.styleable.PostCreatorBoardView_actionsColor)) {
                attributes.getColorStateList(R.styleable.PostCreatorBoardView_actionsColor)
                    ?.let(this::setActionsColor)
            }

            if (attributes.hasValue(R.styleable.PostCreatorBoardView_actionsBorderWidth)) {
                setActionsBorderWidth(attributes.getDimensionPixelSize(
                    R.styleable.PostCreatorBoardView_actionsBorderWidth, 0))
            }

            if (attributes.hasValue(R.styleable.PostCreatorBoardView_textColor)) {
                textColor = attributes.getColor(
                    R.styleable.PostCreatorBoardView_textColor, textInput.currentTextColor)
            }

            if (attributes.hasValue(R.styleable.PostCreatorBoardView_hintTextColor)) {
                hintTextColor = attributes.getColor(
                    R.styleable.PostCreatorBoardView_hintTextColor, textInput.currentHintTextColor)
            }

            if (attributes.hasValue(R.styleable.PostCreatorBoardView_text)) {
                text = attributes.getString(R.styleable.PostCreatorBoardView_text) ?: ""
            }
        } finally {
            attributes.recycle()
        }
    }

    private fun initTextChangingTracking() {
        textInput.addTextChangedListener {
            notifyTextChanged(it?.toString() ?: "")

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

    // region: TextChangedListener

    fun addTextChangedListener(listener: TextChangedListener) {
        textChangedListeners.add(listener)
    }

    private fun notifyTextChanged(text: String) {
        textChangedListeners.forEach {
            it.onTextChanged(text)
        }
    }

    // endregion

    // region: ImageStateListener

    fun addImageStateListener(listener: ImageStateListener) {
        imageStateListeners.add(listener)
    }

    private fun notifyImageTrackingStarted() {
        imageStateListeners.forEach {
            it.onStartTrackingImage()
        }
    }

    private fun notifyImageTrackingStopped() {
        imageStateListeners.forEach {
            it.onStopTrackingImage()
        }
    }

    // endregion

    // region: Add image

    fun addImage(image: PostCreatorImage) {
        if (imageViewsMap.containsKey(image.id))
            return

        val imageView = layoutImage(image)
        imageViewsMap[image.id] = imageView
        imagesMap[image.id] = image

        Glide.with(context)
            .load(image.source)
            .into(imageView)
    }

    private fun layoutImage(image: PostCreatorImage): ImageView {
        val imageView = ImageView(context).apply {
            layoutParams = LayoutParams(0, 0)
        }

        val rect = image.rect

        if (rect.isEmpty) {
            val size = Dimen.dp(92, context).toInt()
            rect.set(0, 0, size, size)
        }

        updateImageView(imageView, image)

        addView(imageView, indexOfChild(textDecorator))
        return imageView
    }

    // endregion

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        deleteButtonCenterPoint.set(
            deleteButton.left + deleteButton.width.toFloat() / 2,
            deleteButton.top + deleteButton.height.toFloat() / 2)
    }

    // region: onTouchEvent

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchTracker = activeTouchTracker ?: findTouchTracker(event)
            ?.also {
                activeTouchTracker = it
                notifyImageTrackingStarted()
            }

        return touchTracker?.onTouchEvent(event) ?: super.onTouchEvent(event)
    }

    private fun findTouchTracker(event: MotionEvent): TouchTracker? {
        val image = findImageUnderTouch(event) ?: return null

        return touchTrackerFactory.create(
            image,
            this::onImageMoved,
            this::onImageInteractionCompleted)
    }

    private fun onImageMoved(touchPoint: PointF, image: PostCreatorImage) {
        val imageView = imageViewsMap[image.id] ?: return

        updateImageView(imageView, image)
        setDeleteActiveActive(isDeleteAction(touchPoint))

        deleteButton.isVisible = true
    }

    private fun updateImageView(imageView: ImageView, image: PostCreatorImage) {
        imageView.updateLayoutParams<LayoutParams> {
            width = image.rect.width()
            height = image.rect.height()

            setMargins(
                image.rect.left,
                image.rect.top,
                0, 0)
        }

        imageView.rotation = image.rotation
    }

    private fun isDeleteAction(touchPoint: PointF): Boolean {
        val distanceToDelete = MathUtil.getDistance(deleteButtonCenterPoint, touchPoint)
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
            bottomMargin = this@PostCreatorBoardView.height - deleteButtonCenterPoint.y.toInt() - size / 2
        }
    }

    private fun onImageInteractionCompleted(touchPoint: PointF, image: PostCreatorImage) {
        notifyImageTrackingStopped()

        activeTouchTracker = null
        deleteButton.isVisible = false

        if (isDeleteAction(touchPoint)) {
            deleteImage(image)
        }
    }

    private fun deleteImage(image: PostCreatorImage) {
        imagesMap.remove(image.id)

        imageViewsMap.remove(image.id)
            ?.let(this::removeView)
    }

    private fun findImageUnderTouch(event: MotionEvent): PostCreatorImage? {
        val rect = Rect()
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()

        return imageViewsMap.entries
            .firstOrNull { (_, imageView) ->
                imageView.getGlobalVisibleRect(rect)
                rect.contains(x, y)
            }
            ?.let { (id, _) -> imagesMap[id]}
    }

    // endregion

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

    interface TextChangedListener {

        fun onTextChanged(text: String)

    }

    interface ImageStateListener {

        fun onStartTrackingImage()

        fun onStopTrackingImage()

    }

}