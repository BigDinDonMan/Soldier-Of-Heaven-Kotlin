package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import com.badlogic.gdx.math.Vector2

class Explosive : PooledComponent() {
    var range = 0f
    var fuseTime = 0f
    var damping = -1f //slow down factor of the rocket
    var strength = 0f
    val moveDirection = Vector2()

    override fun reset() {
        damping = -1f
        fuseTime = 0f
        range = 0f
        strength = 0f
        moveDirection.set(Vector2.Zero)
    }
}
