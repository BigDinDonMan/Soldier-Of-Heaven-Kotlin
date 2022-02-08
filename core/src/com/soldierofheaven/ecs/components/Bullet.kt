package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import com.badlogic.gdx.math.Vector2

class Bullet : PooledComponent() {

    val moveDirection = Vector2()

    override fun reset() {
        moveDirection.set(Vector2.Zero)
    }
}
