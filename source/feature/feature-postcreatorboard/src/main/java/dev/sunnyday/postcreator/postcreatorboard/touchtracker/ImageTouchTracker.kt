package dev.sunnyday.postcreator.postcreatorboard.touchtracker

import android.graphics.Point
import android.graphics.PointF
import android.view.MotionEvent
import dev.sunnyday.postcreator.core.common.math.MathUtil
import dev.sunnyday.postcreator.postcreatorboard.PostCreatorImage
import kotlin.properties.Delegates

internal class ImageTouchTracker(
    private val image: PostCreatorImage,
    private val onClickCallback: (PointF, PostCreatorImage) -> Unit,
    private val onStartMoveCallback: (PointF, PostCreatorImage) -> Unit,
    private val onMoveCallback: (PointF, PostCreatorImage) -> Unit,
    private val onCompleteMoveCallback: (PointF, PostCreatorImage) -> Unit
) : TouchTracker {

    private var anchorPoint = Point()
    private var anchorSize = Point()
    private var anchorSizeRelativePoint = PointF()
    private var anchorAngle = 0f

    private var firstPointerId: Int? = null
    private var firstStartPoint = PointF()
    private var firstActualPoint = PointF()

    private var secondPointerId: Int? = null
    private var secondActualPoint = PointF()

    private var startSize = Point()
    private var startDistance = 0f
    private var startAngle = 0f

    private val sizeBuffer = Point()

    private val isMultiTouch get() = secondPointerId != null

    private var isMoving by Delegates.observable(false) { _, old, new ->
        if (old != new && new) {
            onStartMoveCallback(firstActualPoint, image)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                when {
                    firstPointerId == null -> initFirstPointer(event)
                    secondPointerId == null -> initSecondPointer(event)
                    else -> return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                when {
                    firstPointerId == null -> initFirstPointer(event)
                    secondPointerId == null && event.pointerCount > 1 -> initSecondPointer(event)
                    else -> {
                        updateActualPoints(event)
                        checkMoving()

                        if (isMoving) {
                            onMoved()
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {
                when (event.getPointerId(event.actionIndex)) {
                    firstPointerId -> {
                        if (!isMoving) {
                            onClickCallback(firstActualPoint, image)
                        } else {
                            onCompleteMoveCallback(firstActualPoint, image)
                        }

                        isMoving = false
                        firstPointerId = null
                        secondPointerId = null

                        return false
                    }
                    secondPointerId -> {
                        secondPointerId = null
                    }
                }
            }
        }

        return true
    }

    private fun initFirstPointer(event: MotionEvent) {
        val pointerId = event.getPointerId(0)
        firstPointerId = pointerId

        updatePoint(pointerId, event, firstStartPoint)
        firstActualPoint.set(firstStartPoint)

        anchorSize.set(image.rect.width(), image.rect.height())
        anchorPoint.set(image.rect.left, image.rect.top)
        anchorSizeRelativePoint.set(
            (firstStartPoint.x - image.rect.left) / image.rect.width(),
            (firstStartPoint.y - image.rect.top) / image.rect.height())
    }

    private fun initSecondPointer(event: MotionEvent) {
        val pointerId = (0 until event.pointerCount)
            .map(event::getPointerId)
            .find { it != firstPointerId }
            ?: return

        secondPointerId = pointerId

        updatePoint(pointerId, event, secondActualPoint)

        anchorAngle = image.rotation

        startSize.set(image.rect.width(), image.rect.height())
        startDistance = MathUtil.getDistance(firstActualPoint, secondActualPoint)
        startAngle = MathUtil.getAngle(firstActualPoint, secondActualPoint)

        isMoving = true
    }

    private fun updateActualPoints(event: MotionEvent) {
        firstPointerId?.let { pointerId ->
            updatePoint(pointerId, event, firstActualPoint)
        }
        secondPointerId?.let { pointerId ->
            updatePoint(pointerId, event, secondActualPoint)
        }
    }

    private fun updatePoint(pointerId: Int, event: MotionEvent, pointF: PointF) {
        val index = event.findPointerIndex(pointerId)
        pointF.set(event.getX(index), event.getY(index))
    }

    private fun checkMoving() {
        if (isMoving) return

        val touchDistance = MathUtil.getDistance(firstStartPoint, firstActualPoint)
        if (touchDistance > 10) {
            firstStartPoint.set(firstActualPoint)
            isMoving = true
        }
    }

    private fun onMoved() {
        val size = sizeBuffer
        size.set(image.rect.width(), image.rect.height())

        val secondActualPoint = secondActualPoint

        if (isMultiTouch) {
            val actualDistance = MathUtil.getDistance(firstActualPoint, secondActualPoint)
            val sizeRatio = actualDistance / startDistance

            size.set(
                (startSize.x * sizeRatio).toInt(),
                (startSize.y * sizeRatio).toInt())

            val angle = MathUtil.getAngle(firstActualPoint, secondActualPoint)

            val resultRotation = anchorAngle + angle - startAngle
            if (resultRotation != image.rotation) {
                image.rotation = resultRotation
            }
        }

        val dx = firstActualPoint.x - firstStartPoint.x
        val dSizeX = size.x - anchorSize.x
        val xSizeShift = dSizeX * anchorSizeRelativePoint.x
        val left = (anchorPoint.x + dx - xSizeShift).toInt()

        val dy = firstActualPoint.y - firstStartPoint.y
        val dSizeY = size.y - anchorSize.y
        val ySizeShift = dSizeY * anchorSizeRelativePoint.y
        val top = (anchorPoint.y + dy - ySizeShift).toInt()

        image.rect.set(left, top, left + size.x, top + size.y)

        onMoveCallback(firstActualPoint, image)
    }

}