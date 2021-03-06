package com.soldierofheaven.util.math

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import kotlin.math.pow
import kotlin.math.sqrt

fun rotationTowards(directionX: Float, directionY: Float): Float {
    return MathUtils.atan2(directionY, directionX) * MathUtils.radiansToDegrees
}

fun euclideanDistance(x1: Float, y1: Float, x2: Float, y2: Float) =
    sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2))

fun Vector2.directionTowards(x: Float, y: Float, out: Vector3): Vector3 {
    out.set(x - this.x, y - this.y, out.z)
    return out
}

fun cbrt(v: Float) = v.pow(1/3f)

fun clamp(value: Int, min: Int, max: Int): Int {
    if (value > max) return max
    if (value < min) return min
    return value
}

fun clamp(value: Long, min: Long, max: Long): Long {
    if (value > max) return max
    if (value < min) return min
    return value
}

fun clamp(value: Float, min: Float, max: Float): Float {
    if (value > max) return max
    if (value < min) return min
    return value
}

fun clamp(value: Double, min: Double, max: Double): Double {
    if (value > max) return max
    if (value < min) return min
    return value
}
