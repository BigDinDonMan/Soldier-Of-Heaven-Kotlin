package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent

class Health : PooledComponent() {
    var maxHealth = 150f
        set(value) {
            if (health > value) {
                health = value
            }
            field = value
        }
    var health = maxHealth

    override fun reset() {
        maxHealth = 150f
        health = maxHealth
    }
}
