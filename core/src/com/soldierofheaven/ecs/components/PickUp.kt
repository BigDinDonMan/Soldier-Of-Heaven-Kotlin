package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import com.soldierofheaven.ecs.components.enums.PickUpType

class PickUp : PooledComponent() {
    var pickUpType = PickUpType.HEALTH
    //todo: come up with payload... either an Any object with different structs as actual payload or something else, like class with implemented interface

    override fun reset() {
        pickUpType = PickUpType.HEALTH
    }
}
