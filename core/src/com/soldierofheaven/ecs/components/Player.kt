package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent

class Player : PooledComponent() {
    var hitTimer = 0f
    var currentHitTimer = 0f

    var shoveTimer = 0f
    var currentShoveTimer = 0f
    var shoveRange = 0f

    override fun reset() {
        hitTimer = 0f
        currentHitTimer = 0f
        shoveRange = 0f
        shoveTimer = 0f
        currentShoveTimer = 0f
    }
}
