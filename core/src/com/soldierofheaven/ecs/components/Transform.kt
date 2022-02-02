package com.soldierofheaven.ecs.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

class Transform : Component() {
    val position = Vector3()
    val size = Vector2()
    private val center = Vector2()
        get() {
            center.set(position.x + size.x / 2, position.y + size.y / 2)
            return field
        }
    val rotation = 0f
}
