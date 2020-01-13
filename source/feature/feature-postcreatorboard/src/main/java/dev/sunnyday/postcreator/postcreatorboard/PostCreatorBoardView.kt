package dev.sunnyday.postcreator.postcreatorboard

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.withSave
import androidx.core.view.*
import androidx.core.widget.addTextChangedListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.core.common.android.InputStreamUtil
import dev.sunnyday.postcreator.core.common.math.MathUtil
import dev.sunnyday.postcreator.postcreatorboard.decorations.TextDecorator
import dev.sunnyday.postcreator.postcreatorboard.touchtracker.ImageTouchTrackerFactory
import dev.sunnyday.postcreator.postcreatorboard.touchtracker.TouchTracker
import kotlinx.android.synthetic.main.postcreatorboard__view.view.*
import kotlin.math.max
import kotlin.math.min


class PostCreatorBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?  = null, defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    @get:ColorInt
    var textColor: Int
        get() = textInput.currentTextColor
        set(@ColorInt value) { textInput.setTextColor(value) }

    @get:ColorInt
    var hintTextColor: Int
        get() = textInput.currentHintTextColor
        set(@ColorInt value) { textInput.setHintTextColor(value) }

    val images: List<PostCreatorImage>
        get() = imageItems.map { it.data }

    var text: String
        get() = textInput.text.toString()
        set(value) { textInput.setText(value) }

    private var actionsColors: ColorStateList = ColorStateList.valueOf(Color.BLACK)
    private var actionsBorderWidth: Int = Dimen.dp(2, context).toInt()

    private val imageItems = mutableListOf<ImageItem>()

    private var activeTouchTracker: TouchTracker? = null
    private val touchTrackerFactory = ImageTouchTrackerFactory()
    private val checkTouchSize = Dimen.dp(8, context).toInt()

    private val rotationMatrixBuffer = Matrix()

    private var deleteButtonCenterPointBuffer: PointF = PointF(0f, 0f)
    private val deleteActionRadius = Dimen.dp(36, context)
    private var isDeleteActionActive = false
    private var deleteButtonMoveAnimation: Animator? = null
    private var deleteButtonSizeAnimation: Animator? = null

    private var textChangedListeners = mutableSetOf<TextChangedListener>()

    private var imageStateListeners = mutableSetOf<ImageStateListener>()

    override fun setEnabled(isEnabled: Boolean) {
        super.setEnabled(isEnabled)
        textInput.isEnabled = isEnabled
    }

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.postcreatorboard__view, this, true)

        deleteButton.isInvisible = true

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
        textInput.setTextDecorators(decorators)
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
        if (containsImage(image))
            return

        val drawable = resolveDrawable(image)
            ?: return

        updateImageRect(image.rect, drawable)
        imageItems.add(ImageItem(image, drawable))

        invalidate()
    }

    private fun containsImage(image: PostCreatorImage): Boolean =
        imageItems.any { it.data.id == image.id }

    private fun updateImageRect(rect: Rect, drawable: Drawable) {
        if (drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0)
            return

        val drawableRatio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
        val imageRatio = rect.width().toFloat() / rect.height().toFloat()

        if (drawableRatio != imageRatio) {
            val ratioRatio = drawableRatio / imageRatio
            val width = (rect.width() * ratioRatio).toInt()
            val horizontalInset = (rect.width() - width) / 2
            rect.inset(horizontalInset, 0)
        }
    }

    private fun resolveDrawable(image: PostCreatorImage): Drawable? {
        val stream = InputStreamUtil.inputStreamFromUri(image.source, context)
            ?: return null

        return Drawable.createFromStream(stream, image.id.toString())
    }

    // endregion

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        updateDeleteButtonTranslationByVisibility()
    }

    private fun updateDeleteButtonTranslationByVisibility() {
        if (deleteButton.isInvisible) {
            deleteButton.translationY = calculateInvisibleDeleteButtonTranslationY()
        }
    }

    private fun calculateInvisibleDeleteButtonTranslationY(): Float =
        (height - deleteButton.top).toFloat()

    override fun onDraw(canvas: Canvas) {
        drawImages(canvas)
        super.onDraw(canvas)
    }

    private fun drawImages(canvas: Canvas) {
        imageItems.forEach { item ->
            canvas.withSave {
                drawImage(item, this)
            }
        }
    }

    private fun drawImage(imageItem: ImageItem, canvas: Canvas) {
        val (image, drawable) = imageItem

        val rect = image.rect
        canvas.translate(rect.left.toFloat(), rect.top.toFloat())
        canvas.rotate(image.rotation, rect.width() / 2f, rect.height() / 2f)

        drawable.setBounds(0, 0, rect.width(), rect.height())
        drawable.draw(canvas)
    }

    // region: onTouchEvent

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (textInput.selectionEnd != textInput.selectionStart)
            return super.dispatchTouchEvent(event)

        val touchTracker = activeTouchTracker ?: findTouchTracker(event)
            ?.also {
                activeTouchTracker = it
                notifyImageTrackingStarted()
            }

        return touchTracker?.onTouchEvent(event) ?: super.dispatchTouchEvent(event)
    }

    private fun findTouchTracker(event: MotionEvent): TouchTracker? {
        val image = findImageItemUnderTouch(event) ?: return null

        return touchTrackerFactory.create(
            image.data,
            onClick = { point, _ -> onImageClick(point) },
            onStartMove = { _, _ -> onImageStartMove() },
            onMove = { point, _ -> onImageMove(point) },
            onCompleteMove = this::onImageInteractionComplete)
    }

    private fun onImageClick(point: PointF) {
        simulateTapOnView(point, textInput)
    }

    private fun simulateTapOnView(point: PointF, view: View) {
        view.onTouchEvent(MotionEvent.obtain(
            SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
            MotionEvent.ACTION_DOWN, point.x, point.y, 0))

        view.onTouchEvent(MotionEvent.obtain(
            SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
            MotionEvent.ACTION_UP, point.x, point.y, 0))
    }

    private fun onImageStartMove() {
        showDeleteButton()
    }

    private fun onImageMove(touchPoint: PointF) {
        invalidate()
        setDeleteButtonActive(isDeleteAction(touchPoint))
    }

    private fun isDeleteAction(touchPoint: PointF): Boolean {
        deleteButtonCenterPointBuffer.set(
            (deleteButton.left + deleteButton.right) * 0.5f,
            (deleteButton.top + deleteButton.bottom) * 0.5f)

        val distanceToDelete = MathUtil.getDistance(deleteButtonCenterPointBuffer, touchPoint)
        return distanceToDelete <= deleteActionRadius
    }

    private fun setDeleteButtonActive(isActive: Boolean) {
        if (isDeleteActionActive == isActive) return
        isDeleteActionActive = isActive

        updateDeleteButtonLayoutByActiveState(isActive)
    }

    private fun updateDeleteButtonLayoutByActiveState(isActive: Boolean) {
        if (isActive) {
            val size =  Dimen.dp(56, context).toInt()
            deleteButton.setImageResource(R.drawable.postcreator__ic__fab_trash_released)
            animateSetDeleteButtonSize(size)
        } else {
            val size =  Dimen.dp(48, context).toInt()
            deleteButton.setImageResource(R.drawable.postcreator__ic__fab_trash)
            animateSetDeleteButtonSize(size)
        }
    }

    private fun animateSetDeleteButtonSize(size: Int) {
        if (deleteButton.width == size) return

        deleteButtonSizeAnimation?.cancel()

        deleteButtonSizeAnimation = ValueAnimator.ofInt(deleteButton.width, size).apply {
            addUpdateListener {
                val animatedSize = it.animatedValue as Int

                deleteButton.updateLayoutParams<LayoutParams> {
                    width = animatedSize
                    height = animatedSize
                }
            }

            duration = 125
            start()
        }
    }

    private fun onImageInteractionComplete(touchPoint: PointF, image: PostCreatorImage) {
        notifyImageTrackingStopped()

        activeTouchTracker = null

        if (isInvisible(image.rect) || isDeleteAction(touchPoint)) {
            deleteImage(image)
        }

        hideDeleteButton()
    }

    private fun isInvisible(rect: Rect): Boolean {
        val maxRotatedWidthOffset = (rect.width() * 0.16f).toInt()
        val maxRotatedHeightOffset = (rect.height() * 0.16f).toInt()

        return rect.isEmpty || !rect.intersects(
            -maxRotatedWidthOffset, -maxRotatedHeightOffset,
            width + maxRotatedWidthOffset, height + maxRotatedHeightOffset)
    }

    private fun deleteImage(image: PostCreatorImage) {
        val item = imageItems.firstOrNull { it.data.id == image.id }
            ?: return

        imageItems.remove(item)
        invalidate()
    }

    private fun findImageItemUnderTouch(event: MotionEvent): ImageItem? {
        val x = event.x.toInt()
        val y = event.y.toInt()

        val itemsUnderTouch = imageItems
            .reversed()
            .filter { checkIsUnderTouch(x, y, it) }

        if (itemsUnderTouch.isEmpty())
            return null

        return getFirstNonTransparentTouchedImage(x, y, itemsUnderTouch)
    }

    private fun checkIsUnderTouch(x: Int, y: Int, imageItem: ImageItem): Boolean =
        getLocationInRotatedRect(x, y, imageItem.data.rect, -imageItem.data.rotation) != null

    private fun getLocationInRotatedRect(x: Int, y: Int, rect: Rect, degrees: Float): FloatArray? {
        val location = floatArrayOf(0f, 0f)
        location[0] = x - rect.exactCenterX()
        location[1] = y - rect.exactCenterY()

        rotateVector(location, degrees)

        location[0] = location[0] + rect.width() / 2
        location[1] = location[1] + rect.height() / 2

        return if (checkInRect(location, rect.width(), rect.height())) {
            location
        } else {
            null
        }
    }

    private fun checkInRect(location: FloatArray, width: Int, height: Int): Boolean =
        location[0] >= 0 && location[0] < width && location[1] >= 0 && location[1] < height

    private fun getFirstNonTransparentTouchedImage(
        x: Int, y: Int, items: List<ImageItem>
    ): ImageItem? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val touchedImage = items.firstOrNull {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            drawImage(it, canvas)

            checkNonTransparentTouch(x, y, bitmap)
        }

        bitmap.recycle()

        return touchedImage
    }

    private fun checkNonTransparentTouch(x: Int, y: Int, bitmap: Bitmap): Boolean {
        val touchAreaPixels = getTouchPixels(x, y, checkTouchSize, bitmap)
        return touchAreaPixels.any { it != Color.TRANSPARENT }
    }

    private fun rotateVector(vector: FloatArray, degrees: Float) {
        val rotationMatrix = rotationMatrixBuffer

        rotationMatrix.reset()
        rotationMatrix.setRotate(degrees, 0f, 0f)
        rotationMatrix.mapVectors(vector)
    }

    private fun getTouchPixels(x: Int, y: Int, size: Int, bitmap: Bitmap): IntArray {
        val halfTouchSize = size / 2
        val touchAreaLeft = max(x - halfTouchSize, 0)
        val touchAreaRight = min(x + halfTouchSize, bitmap.width)
        val width = touchAreaRight - touchAreaLeft
        val touchAreaTop = max(y - halfTouchSize, 0)
        val touchAreaBottom = min(y + halfTouchSize, bitmap.height)
        val height = touchAreaBottom - touchAreaTop

        if (height <= 0 || width <= 0)
            return intArrayOf()

        val touchAreaPixels = IntArray(width * height) {
            Color.TRANSPARENT
        }
        bitmap.getPixels(touchAreaPixels, 0, width, touchAreaLeft, touchAreaTop, width, height)

        return touchAreaPixels
    }

    // endregion

    // region: Delete button animation

    private fun showDeleteButton() {
        deleteButtonMoveAnimation?.cancel()

        val startTranslationY = deleteButton.translationY
        val endTranslationY = 0f

        deleteButtonMoveAnimation = ValueAnimator.ofFloat(startTranslationY, endTranslationY).apply {
            addUpdateListener { value ->
                deleteButton.translationY = value.animatedValue as Float
            }

            doOnStart {
                deleteButton.setWillNotDraw(true)
                deleteButton.isVisible = true
            }

            interpolator = FastOutSlowInInterpolator()
            duration = 250

            start()
        }
    }

    private fun hideDeleteButton() {
        deleteButtonMoveAnimation?.cancel()

        val startTranslationY = deleteButton.translationY
        val endTranslationY = calculateInvisibleDeleteButtonTranslationY()

        deleteButtonMoveAnimation = ValueAnimator.ofFloat(startTranslationY, endTranslationY).apply {
            addUpdateListener { value ->
                deleteButton.translationY = value.animatedValue as Float
            }

            doOnEnd {
                deleteButton.isInvisible = true
            }

            interpolator = AccelerateInterpolator()
            duration = 250

            start()
        }
    }

    // endregion

    interface TextChangedListener {

        fun onTextChanged(text: String)

    }

    interface ImageStateListener {

        fun onStartTrackingImage()

        fun onStopTrackingImage()

    }

    private data class ImageItem(
        val data: PostCreatorImage,
        val drawable: Drawable)

}