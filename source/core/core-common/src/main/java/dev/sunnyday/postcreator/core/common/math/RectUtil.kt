package dev.sunnyday.postcreator.core.common.math

import android.graphics.Matrix
import android.graphics.Rect

object RectUtil {

    fun getLocationInRotatedRect(
        x: Int,
        y: Int,
        rect: Rect,
        degrees: Float,
        locationVector: FloatArray = floatArrayOf(0f, 0f),
        rotationMatrixBuffer: Matrix = Matrix()
    ): FloatArray? {
        locationVector[0] = x - rect.exactCenterX()
        locationVector[1] = y - rect.exactCenterY()

        VectorUtil.rotate(locationVector, degrees, rotationMatrixBuffer)

        locationVector[0] = locationVector[0] + rect.width() / 2
        locationVector[1] = locationVector[1] + rect.height() / 2

        return if (checkInRect(locationVector, rect.width(), rect.height())) {
            locationVector
        } else {
            null
        }
    }

    private fun checkInRect(location: FloatArray, width: Int, height: Int): Boolean =
        location[0] >= 0 && location[0] < width && location[1] >= 0 && location[1] < height

}