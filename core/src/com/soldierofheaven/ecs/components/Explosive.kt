package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import java.util.*

class Explosive : PooledComponent() {
    var range = 0f
    var fuseTime = 0f
    var explodeOnImpact = false
    var damping = 0f //slow down factor of the explosive

    override fun reset() {

    }
}
