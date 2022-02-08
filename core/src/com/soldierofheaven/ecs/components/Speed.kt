package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent

class Speed : PooledComponent() {
    var value = 0f

    override fun reset() {
        value = 0f
    }
}
