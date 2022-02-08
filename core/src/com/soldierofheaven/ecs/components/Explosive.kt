package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import java.util.*

class Explosive : PooledComponent() {

    enum class Type {
        GRENADE,
        MISSILE
    }

    var range = 0f
    var fuseTime = 0f
    var explodeOnImpact = false
    var damping = -1f //slow down factor of the rocket
    var explosiveType = Type.GRENADE
        set(value) {
            field = value
            explodeOnImpact = value == Type.MISSILE
        }

    override fun reset() {
        explosiveType = Type.GRENADE
        damping = -1f
        explodeOnImpact = false
        fuseTime = 0f
        range = 0f
    }
}
