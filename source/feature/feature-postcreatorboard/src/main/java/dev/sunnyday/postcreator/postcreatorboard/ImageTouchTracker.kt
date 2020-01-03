package dev.sunnyday.postcreator.postcreatorboard

import android.graphics.Point
import android.graphics.PointF
import android.view.MotionEvent
import androidx.core.graphics.toPoint
import timber.log.Timber
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

internal class ImageTouchTracker(
    private val image: PostCreatorImage,
    onMoved: (Point, PostCreatorImage) -> Unit,
    onComplete: (Point, PostCreatorImage) -> Unit
) {

    private var anchorPoint: Point? = null
    private var anchorSize: Int? = null
    private var anchorAngle: Float? = null

    private var firstPointerId: Int? = null
    private var firstStartPoint: PointF? = null
    private var firstActualPoint: PointF? = null

    private var secondPointerId: Int? = null
    private var secondTouchPoint: PointF? = null
    private var secondActualPoint: PointF? = null

    private var startSize: Int? = null
    private var startDistance: Float? = null
    private var startAngle: Float? = null

    private val onMovedCallback = onMoved
    private val onCompleteCallback = onComplete

    fun onTouchEvent(event: MotionEvent): Boolean {
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
                        firstPointerId?.let {
                            firstActualPoint = getTouchPoint(it, event)
                        }
                        secondPointerId?.let {
                            secondActualPoint = getTouchPoint(it, event)
                        }

                        onMoved()
                    }
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {
                when (event.getPointerId(event.actionIndex)) {
                    firstPointerId -> {
                        val point = firstActualPoint
                        if (point != null) {
                            onCompleteCallback(point.toPoint(), image)
                        }

                        firstPointerId = null
                        firstStartPoint = null
                        firstActualPoint = null
                        secondPointerId = null
                        secondTouchPoint = null
                        secondActualPoint = null
                        startDistance = null
                        return false
                    }
                    secondPointerId -> {
                        secondPointerId = null
                        secondTouchPoint = null
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

        val touchPoint = getTouchPoint(pointerId, event)
        firstStartPoint = touchPoint
        firstActualPoint = touchPoint

        anchorSize = image.rect.width()
        anchorPoint =
            Point(image.rect.left, image.rect.top)
    }

    private fun initSecondPointer(event: MotionEvent) {
        val pointerId = (0 until event.pointerCount)
            .map(event::getPointerId)
            .find { it != firstPointerId }
            ?: return

        secondPointerId = pointerId

        Timber.d("First pointer initialized: $secondPointerId")

        val touchPoint = getTouchPoint(pointerId, event)
        secondTouchPoint = touchPoint
        secondActualPoint = touchPoint

        startSize = image.rect.width()

        val firstTouchPoint = firstActualPoint ?: return
        startDistance = getDistanceBetweenPoints(firstTouchPoint, touchPoint)
        startAngle = getAngleBetweenPoints(firstTouchPoint, touchPoint)
        anchorAngle = image.rotation
    }

    private fun onMoved() {
        val anchorPoint = anchorPoint ?:return
        val firstActualPoint = firstActualPoint ?:return
        val firstStartPoint = firstStartPoint ?:return
        val anchorSize = anchorSize ?: return

        var size = image.rect.width()

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
        val left = anchorPoint.x + (firstActualPoint.x - firstStartPoint.x).toInt() - sizeShift
        val top = anchorPoint.y + (firstActualPoint.y - firstStartPoint.y).toInt() - sizeShift
        image.rect.set(left, top, left + size, top + size)

        onMovedCallback(firstActualPoint.toPoint(), image)
    }

    private fun getTouchPoint(id: Int, event: MotionEvent): PointF {
        val index = event.findPointerIndex(id)
        return PointF(event.getX(index), event.getY(index))
    }

    companion object {

        private fun getDistanceBetweenPoints(f: PointF, s: PointF): Float =
            sqrt((s.x - f.x).pow(2) + (s.y - f.y).pow(2))

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

}