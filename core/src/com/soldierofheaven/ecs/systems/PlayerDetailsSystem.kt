package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.soldierofheaven.ecs.events.ShoveEvent
import com.soldierofheaven.util.`interface`.PlayerSystem
import net.mostlyoriginal.api.event.common.Subscribe

class PlayerDetailsSystem : BaseSystem(), PlayerSystem {

    private var playerEntityId = 0

    override fun processSystem() {
    }

    @Subscribe
    private fun handleShoveEvent(e: ShoveEvent) {

    }

    override fun setPlayerEntityId(id: Int) {
        playerEntityId = id
    }
}
