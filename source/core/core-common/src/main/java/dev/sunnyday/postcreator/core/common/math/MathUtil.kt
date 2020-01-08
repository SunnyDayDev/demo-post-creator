package dev.sunnyday.postcreator.core.common.math

import android.graphics.PointF
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

object MathUtil {

    fun getDistance(f: PointF, s: PointF): Float =
        sqrt((s.x - f.x).pow(2) + (s.y - f.y).pow(2))

    fun getAngle(f: PointF, s: PointF): Float {
        val atan = atan2((f.y - s.y).toDouble(), (f.x - s.x).toDouble())
        val angle = Math.toDegrees(atan).toFloat()

        return if (angle < 0) {
            angle + 360f
        } else {
            angle
        }
    }

}