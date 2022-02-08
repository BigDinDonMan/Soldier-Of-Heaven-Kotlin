package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import java.util.*

class Explosive : PooledComponent() {
    var range = 0f
    var fuseTime = 0f
    var damping = -1f //slow down factor of the rocket

    override fun reset() {
        damping = -1f
        fuseTime = 0f
        range = 0f
    }
}
