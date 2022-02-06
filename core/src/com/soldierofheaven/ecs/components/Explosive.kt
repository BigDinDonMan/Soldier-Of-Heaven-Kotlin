package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent

class Explosive : PooledComponent() {
    var range = 0f
    var fuseTime = 0f

    override fun reset() {

    }
}
