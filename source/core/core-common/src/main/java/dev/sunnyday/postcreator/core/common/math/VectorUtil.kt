package dev.sunnyday.postcreator.core.common.math

import android.graphics.Matrix

object VectorUtil {

    fun rotate(
        vector: FloatArray,
        degrees: Float,
        rotationMatrixBuffer: Matrix = Matrix()
    ) {
        rotationMatrixBuffer.reset()
        rotationMatrixBuffer.setRotate(degrees, 0f, 0f)
        rotationMatrixBuffer.mapVectors(vector)
    }

}