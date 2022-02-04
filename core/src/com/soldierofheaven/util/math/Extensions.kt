package com.soldierofheaven.util.math

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

fun rotationTowards(directionX: Float, directionY: Float): Float {
    return MathUtils.atan2(directionY, directionX) * MathUtils.radiansToDegrees
}

fun Vector2.directionTowards(x: Float, y: Float, out: Vector3): Vector3 {
    out.set(x - this.x, y - this.y, out.z)
    return out
}
