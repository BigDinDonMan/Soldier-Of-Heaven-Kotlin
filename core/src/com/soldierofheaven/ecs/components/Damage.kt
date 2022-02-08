package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import java.util.*

class Damage : PooledComponent() {
    var value = 0f
    var damageableTags = LinkedList<String>()

    override fun reset() {
        value = 0f
        damageableTags.clear()
    }
}
