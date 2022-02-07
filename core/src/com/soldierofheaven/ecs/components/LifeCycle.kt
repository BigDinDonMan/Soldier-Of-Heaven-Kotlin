package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent

class LifeCycle : PooledComponent() {
    var lifeTime: Float = 0f
    override fun reset() {
        lifeTime = 0f
    }
}
