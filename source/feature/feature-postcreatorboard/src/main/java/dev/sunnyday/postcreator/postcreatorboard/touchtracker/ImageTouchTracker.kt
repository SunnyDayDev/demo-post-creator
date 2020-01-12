package dev.sunnyday.postcreator.postcreatorboard.touchtracker

import android.graphics.Point
import android.graphics.PointF
import android.view.MotionEvent
import dev.sunnyday.postcreator.core.common.math.MathUtil
import dev.sunnyday.postcreator.postcreatorboard.PostCreatorImage

internal class ImageTouchTracker(
    private val image: PostCreatorImage,
    private val onStartedCallback: (PointF, PostCreatorImage) -> Unit,
    private val onMovedCallback: (PointF, PostCreatorImage) -> Unit,
    private val onCompleteCallback: (PointF, PostCreatorImage) -> Unit
) : TouchTracker {

    private var anchorPoint: Point? = null
    private var anchorSize: Point? = null
    private var anchorSizeRelativePoint: PointF? = null
    private var anchorAngle: Float? = null

    private var firstPointerId: Int? = null
    private var firstStartPoint: PointF? = null
    private var firstActualPoint: PointF? = null

    private var secondPointerId: Int? = null
    private var secondActualPoint: PointF? = null

    private var startSize: Point? = null
    private var startDistance: Float? = null
    private var startAngle: Float? = null

    private val sizeBuffer = Point()

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
                        firstPointerId?.let { pointerId ->
                            val point = firstActualPoint ?: PointF()
                                .also(this::firstActualPoint::set)
                            updatePoint(pointerId, event,point)
                        }
                        secondPointerId?.let { pointerId ->
                            val point = secondActualPoint ?: PointF()
                                .also(this::secondActualPoint::set)
                            updatePoint(pointerId, event,point)
                        }

                        onMoved()
                    }
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {
                when (event.getPointerId(event.actionIndex)) {
                    firstPointerId -> {
                        onCompleteCallback(firstActualPoint ?: PointF(), image)

                        firstPointerId = null
                        firstStartPoint = null
                        firstActualPoint = null
                        secondPointerId = null
                        secondActualPoint = null
                        startDistance = null

                        return false
                    }
                    secondPointerId -> {
                        secondPointerId = null
                        secondActualPoint = null
                        startDistance = null
                    }
                }
            }
        }

        return true
    }

    private fun initFirstPointer(event: MotionEvent) {
        val pointerId = event.getPointerId(0)
        firstPointerId = pointerId

        val touchPoint = PointF()
        updatePoint(pointerId, event, touchPoint)
        firstStartPoint = PointF(touchPoint.x, touchPoint.y)
        firstActualPoint = touchPoint

        anchorSize = Point(image.rect.width(), image.rect.height())
        anchorPoint = Point(image.rect.left, image.rect.top)
        anchorSizeRelativePoint = PointF(
            (touchPoint.x - image.rect.left) / image.rect.width(),
            (touchPoint.y - image.rect.top) / image.rect.height())

        onStartedCallback(touchPoint, image)
    }

    private fun initSecondPointer(event: MotionEvent) {
        val pointerId = (0 until event.pointerCount)
            .map(event::getPointerId)
            .find { it != firstPointerId }
            ?: return

        secondPointerId = pointerId

        val touchPoint = PointF()
        updatePoint(pointerId, event, touchPoint)
        secondActualPoint = touchPoint

        startSize = Point(image.rect.width(), image.rect.height())

        val firstTouchPoint = firstActualPoint ?: return
        startDistance = MathUtil.getDistance(firstTouchPoint, touchPoint)
        startAngle = MathUtil.getAngle(firstTouchPoint, touchPoint)
        anchorAngle = image.rotation
    }

    private fun onMoved() {
        val anchorPoint = anchorPoint ?:return
        val firstActualPoint = firstActualPoint ?:return
        val firstStartPoint = firstStartPoint ?:return
        val anchorSize = anchorSize ?: return
        val anchorSizeRelativePoint = anchorSizeRelativePoint ?: return

        val size = sizeBuffer
        size.set(image.rect.width(), image.rect.height())

        val secondActualPoint = secondActualPoint

        if (secondActualPoint != null) {
            val startDistance = startDistance
            val startSize = startSize
            val startAngle = startAngle
            val anchorAngle = anchorAngle

            if (startSize != null && startDistance != null) {
                val actualDistance = MathUtil.getDistance(firstActualPoint, secondActualPoint)
                val sizeRatio = actualDistance / startDistance

                size.set(
                    (startSize.x * sizeRatio).toInt(),
                    (startSize.y * sizeRatio).toInt())
            }

            if (startAngle != null && anchorAngle != null) {
                val angle = MathUtil.getAngle(firstActualPoint, secondActualPoint)

                val resultRotation = anchorAngle + angle - startAngle
                if (resultRotation != image.rotation) {
                    image.rotation = resultRotation
                }
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

        onMovedCallback(firstActualPoint, image)
    }

    private fun updatePoint(pointerId: Int, event: MotionEvent, pointF: PointF) {
        val index = event.findPointerIndex(pointerId)
        pointF.set(event.getX(index), event.getY(index))
    }

}