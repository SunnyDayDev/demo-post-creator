package dev.sunnyday.postcreator.postcreatorboard

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.*
import androidx.core.widget.addTextChangedListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.bumptech.glide.Glide
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.core.common.math.MathUtil
import dev.sunnyday.postcreator.postcreatorboard.decorations.TextDecorator
import dev.sunnyday.postcreator.postcreatorboard.touchtracker.ImageTouchTrackerFactory
import dev.sunnyday.postcreator.postcreatorboard.touchtracker.TouchTracker
import kotlinx.android.synthetic.main.postcreatorboard__view.view.*
import java.util.*
import kotlin.math.max
import kotlin.math.min


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
    private val checkTouchSize = Dimen.dp(8, context).toInt()

    private val locationOnScreenBuffer = intArrayOf(0,0)
    private val imageTouchVectorBuffer = floatArrayOf(0f,0f)
    private val rotationMatrixBuffer = Matrix()

    private var deleteButtonCenterPoint: PointF = PointF(0f, 0f)
    private val deleteActionRadius = Dimen.dp(36, context)
    private var isDeleteActionActive = false
    private var deleteButtonAnimation: Animator? = null

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

        addView(imageView, indexOfChild(textInput))
        return imageView
    }

    // endregion

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        deleteButtonCenterPoint.set(
            deleteButton.left + deleteButton.width / 2f,
            deleteButton.top + deleteButton.height / 2f)

        if (deleteButton.isInvisible) {
            deleteButton.translationY = calculateInvisibleDeleteButtonTranslationY()
        }
    }

    private fun calculateInvisibleDeleteButtonTranslationY(): Float =
        (height - deleteButton.top).toFloat()

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
        val image = findImageUnderTouch(event) ?: return null

        return touchTrackerFactory.create(
            image,
            this::onImageMovingStarted,
            this::onImageMoved,
            this::onImageInteractionCompleted)
    }

    private fun onImageMovingStarted(touchPoint: PointF, image: PostCreatorImage) {
        showDeleteButton()
    }

    private fun onImageMoved(touchPoint: PointF, image: PostCreatorImage) {
        val imageView = imageViewsMap[image.id] ?: return

        updateImageView(imageView, image)
        setDeleteButtonActive(isDeleteAction(touchPoint))
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

    private fun setDeleteButtonActive(isActive: Boolean) {
        if (isDeleteActionActive == isActive) return
        isDeleteActionActive = isActive

        updateDeleteButtonLayoutByActiveState(isActive)
    }

    private fun updateDeleteButtonLayoutByActiveState(isActive: Boolean) {
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

        updateDeleteButtonLayout(size, deleteButtonCenterPoint.y.toInt())
    }

    private fun updateDeleteButtonLayout(size: Int, centerVertical: Int) {
        deleteButton.updateLayoutParams<LayoutParams> {
            width = size
            height = size
            bottomMargin = this@PostCreatorBoardView.height - centerVertical - size / 2
        }
    }

    private fun onImageInteractionCompleted(touchPoint: PointF, image: PostCreatorImage) {
        notifyImageTrackingStopped()

        activeTouchTracker = null

        if (isDeleteAction(touchPoint)) {
            deleteImage(image)
        }

        hideDeleteButton()
    }

    private fun deleteImage(image: PostCreatorImage) {
        imagesMap.remove(image.id)

        imageViewsMap.remove(image.id)
            ?.let(this::removeView)
    }

    private fun findImageUnderTouch(event: MotionEvent): PostCreatorImage? {
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()

        val rect = Rect()

        return imageViewsMap.entries
            .reversed()
            .firstOrNull { (_, imageView) ->
                imageView.getGlobalVisibleRect(rect)

                rect.contains(x, y) && checkNonTransparentTouch(x, y, imageView)
            }
            ?.let { (id, _) -> imagesMap[id]}
    }

    private fun checkNonTransparentTouch(rawX: Int, rawY: Int, imageView: ImageView): Boolean {
        var bitmap: Bitmap? = null
        try {
            imageView.getLocationOnScreen(locationOnScreenBuffer)

            val imageX = rawX - locationOnScreenBuffer[0]
            val imageY = rawY - locationOnScreenBuffer[1]

            imageTouchVectorBuffer[0] = imageX.toFloat()
            imageTouchVectorBuffer[1] = imageY.toFloat()

            rotationMatrixBuffer.reset()
            rotationMatrixBuffer.setRotate(
                -imageView.rotation, imageView.width / 2f, imageView.height / 2f)
            rotationMatrixBuffer.mapVectors(imageTouchVectorBuffer)

            val rotatedImageX = imageTouchVectorBuffer[0].toInt()
            val rotatedImageY = imageTouchVectorBuffer[1].toInt()

            bitmap = imageView.drawToBitmap(Bitmap.Config.ARGB_8888)
            val touchAreaPixels =
                getTouchPixels(rotatedImageX, rotatedImageY, checkTouchSize, bitmap)

            return touchAreaPixels.any { it != Color.TRANSPARENT }
        } catch (e: Throwable) {
            return false
        } finally {
            bitmap?.recycle()
        }
    }

    private fun getTouchPixels(x: Int, y: Int, size: Int, bitmap: Bitmap): IntArray {
        val halfTouchSize = size / 2
        val touchAreaLeft = max(x - halfTouchSize, 0)
        val touchAreaRight = min(x + halfTouchSize, bitmap.width)
        val width = touchAreaRight - touchAreaLeft
        val touchAreaTop = max(y - halfTouchSize, 0)
        val touchAreaBottom = min(y + halfTouchSize, bitmap.height)
        val height = touchAreaBottom - touchAreaTop

        val touchAreaPixels = IntArray(width * height) {
            Color.TRANSPARENT
        }
        bitmap.getPixels(touchAreaPixels, 0, width, touchAreaLeft, touchAreaTop, width, height)

        return touchAreaPixels
    }

    // endregion

    // region: Delete button animation

    private fun showDeleteButton() {
        deleteButtonAnimation?.cancel()

        val startTranslationY = deleteButton.translationY
        val endTranslationY = 0f

        deleteButtonAnimation = ValueAnimator.ofFloat(startTranslationY, endTranslationY).apply {
            addUpdateListener { value ->
                deleteButton.translationY = value.animatedValue as Float
            }

            doOnStart {
                deleteButton.isVisible = true
            }

            doOnEnd {
                updateDeleteButtonLayoutByActiveState(isDeleteActionActive)
            }

            interpolator = FastOutSlowInInterpolator()
            duration = 250

            start()
        }
    }

    private fun hideDeleteButton() {
        deleteButtonAnimation?.cancel()

        val startTranslationY = deleteButton.translationY
        val endTranslationY = calculateInvisibleDeleteButtonTranslationY()

        deleteButtonAnimation = ValueAnimator.ofFloat(startTranslationY, endTranslationY).apply {
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

}