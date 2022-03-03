package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import com.soldierofheaven.ecs.components.enums.PickUpType
import com.soldierofheaven.weapons.Weapon

class PickUp : PooledComponent() {
    data class AmmoInfo(val amount: Int, val weapon: Weapon)
    var pickUpType = PickUpType.HEALTH
    var pickUpPayload: Any? = null //either an int or an AmmoInfo instance

    override fun reset() {
        pickUpType = PickUpType.HEALTH
        pickUpPayload = null
    }
}
