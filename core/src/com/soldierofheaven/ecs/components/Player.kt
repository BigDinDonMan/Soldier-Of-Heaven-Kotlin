package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent

class Player : PooledComponent() {
    var hitTimer = 0f
    var currentHitTimer = 0f

    var kickTimer = 0f
    var currentKickTimer = 0f
    var kickRange = 0f
    var kickStrength = 0f
    var kickDamage = 0f

    override fun reset() {
        hitTimer = 0f
        currentHitTimer = 0f
        kickRange = 0f
        kickTimer = 0f
        currentKickTimer = 0f
        kickDamage = 0f
        kickStrength = 0f
    }
}
