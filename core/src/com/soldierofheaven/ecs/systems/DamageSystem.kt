package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.soldierofheaven.EventQueue
import com.soldierofheaven.ecs.components.Health
import com.soldierofheaven.ecs.components.LifeCycle
import com.soldierofheaven.ecs.components.Player
import com.soldierofheaven.ecs.events.DamageEvent
import net.mostlyoriginal.api.event.common.Subscribe
import java.util.*
import kotlin.collections.ArrayList

class DamageSystem : BaseSystem() {

    @Wire
    var healthMapper: ComponentMapper<Health>? = null

    @Wire
    var playerMapper: ComponentMapper<Player>? = null

    private val damageEventQueue = LinkedList<DamageEvent>()

    override fun processSystem() {
        damageEventQueue.forEach { (id, damage) -> kotlin.run {
            val health = healthMapper!!.get(id)
            health.health -= damage
            if (health.health <= 0f) {
                world.edit(id).create(LifeCycle::class.java).apply { lifeTime = -1f }
            }
        } }
    }

    override fun end() {
        damageEventQueue.clear()
    }

    @Subscribe
    private fun receiveDamageEvent(e: DamageEvent) {
        val player = playerMapper!!.get(e.entityId)
        if (player == null) { //add it as usual, its not a player
            damageEventQueue.add(e)
        } else {
            if (player.currentHitTimer > 0f) return
            damageEventQueue.add(e)
            player.currentHitTimer = player.hitTimer
        }
    }
}
