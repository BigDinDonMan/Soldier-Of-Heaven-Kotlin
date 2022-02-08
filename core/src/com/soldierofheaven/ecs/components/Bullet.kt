package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import com.badlogic.gdx.math.Vector2
import com.soldierofheaven.ecs.components.enums.ExplosiveType

class Bullet : PooledComponent() {
    val moveDirection = Vector2()
    var explosionRange: Float? = null
    var explosionTimer: Float? = null
    var explodeOnContact: Boolean? = null
    var explosiveType: ExplosiveType? = null

    override fun reset() {
        moveDirection.set(Vector2.Zero)
        explosionRange = null
        explosionTimer = null
        explodeOnContact = null
        explosiveType = null
    }
}
