package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent

class Health : PooledComponent() {
    var maxHealth = 150f
    var health = maxHealth

    override fun reset() {
        maxHealth = 150f
        health = maxHealth
    }
}
