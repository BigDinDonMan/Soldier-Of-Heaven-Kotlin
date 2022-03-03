package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import com.soldierofheaven.util.math.clamp

class Health : PooledComponent() {
    var maxHealth = 150f
        set(value) {
            if (health > value) {
                health = value
            }
            field = value
        }
    var health = maxHealth
        set(value) {
            field = clamp(value, 0f, maxHealth)
        }

    override fun reset() {
        maxHealth = 150f
        health = maxHealth
    }
}
