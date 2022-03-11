package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent

//a component for enemies that can deal damage both through contact and shooting (to differentiate between them)
class ContactDamage : PooledComponent() {

    var value = 0f
    var knockback = 0f

    override fun reset() {
        value = 0f
        knockback = 0f
    }
}
