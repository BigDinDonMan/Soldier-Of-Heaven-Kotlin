package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.soldierofheaven.EventQueue
import com.soldierofheaven.ecs.components.Health
import com.soldierofheaven.ecs.components.LifeCycle
import com.soldierofheaven.ecs.events.DamageEvent
import net.mostlyoriginal.api.event.common.Subscribe
import java.util.*
import kotlin.collections.ArrayList

class DamageSystem : BaseSystem() {

    @Wire
    var healthMapper: ComponentMapper<Health>? = null

    private val damageEventQueue = LinkedList<DamageEvent>()
    private val processingQueue = LinkedList<DamageEvent>()

    override fun begin() {
        processingQueue += damageEventQueue
        damageEventQueue.clear()
    }

    override fun processSystem() {
        processingQueue.forEach { (id, damage) -> kotlin.run {
            val health = healthMapper!!.get(id)
            health.health -= damage
            if (health.health <= 0f) {
                world.edit(id).create(LifeCycle::class.java).apply { lifeTime = -1f }
            }
        } }
    }

    override fun end() {
        processingQueue.clear()
    }

    @Subscribe
    private fun receiveDamageEvent(e: DamageEvent) {
        damageEventQueue.add(e)
    }
}
