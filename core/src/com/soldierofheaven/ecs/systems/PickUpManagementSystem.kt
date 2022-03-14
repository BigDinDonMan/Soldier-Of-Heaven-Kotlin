package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.soldierofheaven.ecs.events.EnemyKilledEvent
import net.mostlyoriginal.api.event.common.Subscribe

class PickUpManagementSystem : BaseSystem() {
    override fun processSystem() {

    }

    @Subscribe
    private fun trySpawningPickUp(e: EnemyKilledEvent) {

    }
}
