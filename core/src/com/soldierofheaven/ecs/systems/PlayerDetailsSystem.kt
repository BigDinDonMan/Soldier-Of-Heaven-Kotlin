package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.soldierofheaven.EventQueue
import com.soldierofheaven.ecs.components.Player
import com.soldierofheaven.ecs.events.DamageEvent
import com.soldierofheaven.ecs.events.KnockbackEvent
import com.soldierofheaven.ecs.events.ShoveEvent
import com.soldierofheaven.util.PhysicsWorld
import com.soldierofheaven.util.`interface`.PlayerSystem
import net.mostlyoriginal.api.event.common.Subscribe

class PlayerDetailsSystem : BaseSystem(), PlayerSystem {

    private var playerEntityId = 0

    @Wire
    private var playerMapper: ComponentMapper<Player>? = null

    @Wire(name = "physicsWorld")
    private var physicsWorld: PhysicsWorld? = null

    override fun processSystem() {
        val delta = world.delta
        val player = playerMapper!!.get(playerEntityId)

        if (player.currentKickTimer >= 0f) {
            player.currentKickTimer -= delta
        }

        if (player.currentHitTimer >= 0f) {
            player.currentHitTimer -= delta
        }
    }

    @Subscribe
    private fun handleShoveEvent(e: ShoveEvent) {
        val player = playerMapper!!.get(playerEntityId)
        if (player.currentKickTimer > 0f) return
        //todo: raycast in the direction of mouse and with player shoveRange, if something is hit then fetch all hit entity ids and
        //dispatch events
        EventQueue.dispatchMultiple(
//            DamageEvent(),
//            KnockbackEvent()
        ) //todo: dispatch animation change event, damage event and knockback event
    }

    override fun setPlayerEntityId(id: Int) {
        playerEntityId = id
    }
}
