package com.soldierofheaven.ecs.components

import com.artemis.Component
import com.artemis.PooledComponent
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

class Transform : PooledComponent() {
    val position = Vector3()
    val size = Vector2()
    val scale = Vector2(1f, 1f)
    val center = Vector2()
        get() {
            field.set(position.x + size.x / 2, position.y + size.y / 2)
            return field
        }
    var rotation = 0f

    override fun reset() {
        position.set(0f, 0f, 0f)
        size.set(0f, 0f)
        scale.set(1f, 1f)
        center.set(0f, 0f)
        rotation = 0f
    }
}
