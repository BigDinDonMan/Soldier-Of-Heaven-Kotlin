package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import com.badlogic.gdx.math.Vector2

class Bullet : PooledComponent() {

    val moveDirection = Vector2()
    var damage = 0f
    var speed = 0f
    /*todo: extract this to separate component maybe? together with damage?*/
    var damageableEntityTag = ""

    override fun reset() {
        moveDirection.set(Vector2.Zero)
        damageableEntityTag = ""
    }
}
