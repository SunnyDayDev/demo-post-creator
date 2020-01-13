package dev.sunnyday.postcreator.postcreatorboard.touchtracker

import android.graphics.Matrix
import android.graphics.Point
import android.graphics.PointF
import android.view.MotionEvent
import dev.sunnyday.postcreator.core.common.math.MathUtil
import dev.sunnyday.postcreator.core.common.math.VectorUtil
import dev.sunnyday.postcreator.postcreatorboard.PostCreatorImage
import kotlin.properties.Delegates

internal class ImageTouchTracker(
    private val image: PostCreatorImage,
    private val onClickCallback: (PointF, PostCreatorImage) -> Unit,
    private val onStartMoveCallback: (PointF, PostCreatorImage) -> Unit,
    private val onMoveCallback: (PointF, PostCreatorImage) -> Unit,
    private val onCompleteMoveCallback: (PointF, PostCreatorImage) -> Unit
) : TouchTracker {

    private var anchorCenter = PointF()
    private var anchorSize = Point()
    private var anchorPivotToCenterPoint = PointF()
    private var anchorRotation = 0f

    private val centerPivotShift = PointF()
    private var scale = 1f

    private var firstPointerId: Int? = null
    private var firstStartPoint = PointF()
    private var firstActualPoint = PointF()

    private var secondPointerId: Int? = null
    private var secondActualPoint = PointF()
    private var startDistance = 0f
    private var startRotation = 0f
    private var startRotationAngle = 0f
    private var startScale = 1f

    private val isMultiTouch get() = secondPointerId != null

    private var isMoving by Delegates.observable(false) { _, old, new ->
        if (old != new && new) {
            onStartMoveCallback(firstActualPoint, image)
        }
    }

    private val rotateVectorBuffer = floatArrayOf(0f, 0f)
    private val rotateMatrixBuffer = Matrix()

    override fun onTouchEvent(event: MotionEvent): Boolean = when (event.actionMasked) {
        MotionEvent.ACTION_DOWN,
        MotionEvent.ACTION_POINTER_DOWN -> onActionDown(event)

        MotionEvent.ACTION_MOVE -> onActionMove(event)

        MotionEvent.ACTION_UP,
        MotionEvent.ACTION_POINTER_UP -> onActionUp(event)

        else -> true
    }

    private fun onActionDown(event: MotionEvent): Boolean {
        when {
            firstPointerId == null -> initFirstPointer(event)
            secondPointerId == null -> initSecondPointer(event)
        }

        return true
    }

    private fun initFirstPointer(event: MotionEvent) {
        val pointerId = event.getPointerId(0)
        firstPointerId = pointerId

        updatePoint(pointerId, event, firstStartPoint)
        firstActualPoint.set(firstStartPoint)

        anchorSize.set(image.rect.width(), image.rect.height())
        anchorCenter.set(image.rect.exactCenterX(), image.rect.exactCenterY())
        anchorRotation = image.rotation
        anchorPivotToCenterPoint.set(
            image.rect.exactCenterX() - firstActualPoint.x,
            image.rect.exactCenterY() - firstActualPoint.y)
    }

    private fun initSecondPointer(event: MotionEvent) {
        val pointerId = (0 until event.pointerCount)
            .map(event::getPointerId)
            .find { it != firstPointerId }
            ?: return

        secondPointerId = pointerId

        updatePoint(pointerId, event, secondActualPoint)

        startDistance = MathUtil.getDistance(firstActualPoint, secondActualPoint)
        startRotationAngle = MathUtil.getAngle(firstActualPoint, secondActualPoint)
        startRotation = image.rotation
        startScale = scale

        isMoving = true
    }

    private fun onActionMove(event: MotionEvent): Boolean {
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

        return true
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
        if (isMultiTouch) {
            handleMultitouch()
        }

        val dx = firstActualPoint.x - firstStartPoint.x
        val x = (anchorCenter.x + dx + centerPivotShift.x).toInt()

        val dy = firstActualPoint.y - firstStartPoint.y
        val y = (anchorCenter.y + dy + centerPivotShift.y).toInt()

        val halfSizeX = (anchorSize.x * scale).toInt() / 2
        val halfSizeY = (anchorSize.y * scale).toInt() / 2
        image.rect.set(
            x - halfSizeX, y - halfSizeY, x + halfSizeX, y + halfSizeY)

        onMoveCallback(firstActualPoint, image)
    }

    private fun handleMultitouch() {
        handleScaling()
        handleRotation()
    }

    private fun handleScaling() {
        val actualDistance = MathUtil.getDistance(firstActualPoint, secondActualPoint)
        val gestureScale = actualDistance / startDistance
        scale = startScale * gestureScale
    }

    private fun handleRotation() {
        val rotationAngle = MathUtil.getAngle(firstActualPoint, secondActualPoint)
        val dGestureRotation = rotationAngle - startRotationAngle
        val resultRotation = (startRotation + dGestureRotation) % 360

        if (resultRotation == image.rotation)
            return

        image.rotation = resultRotation

        val resultPivotToCenterVector = rotateVectorBuffer
        rotateVectorBuffer[0] = scale * anchorPivotToCenterPoint.x
        rotateVectorBuffer[1] = scale * anchorPivotToCenterPoint.y

        val dRotation = resultRotation - anchorRotation
        VectorUtil.rotate(
            vector = resultPivotToCenterVector,
            degrees = dRotation,
            rotationMatrixBuffer = rotateMatrixBuffer)

        centerPivotShift.set(
            resultPivotToCenterVector[0] - anchorPivotToCenterPoint.x,
            resultPivotToCenterVector[1] - anchorPivotToCenterPoint.y)
    }

    private fun onActionUp(event: MotionEvent): Boolean {
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

        return true
    }

}