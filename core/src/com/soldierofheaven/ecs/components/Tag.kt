package com.soldierofheaven.ecs.components

import com.artemis.Component
import com.artemis.PooledComponent

class Tag : PooledComponent() {
    var value = ""
    override fun reset() {
        value = ""
    }
}
