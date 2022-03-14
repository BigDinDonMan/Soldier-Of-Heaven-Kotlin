package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.soldierofheaven.ecs.components.Enemy
import com.soldierofheaven.ecs.events.EnemyKilledEvent
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.random.Random

class PickUpManagementSystem : BaseSystem() {

    @Wire
    private var enemyMapper: ComponentMapper<Enemy>? = null

    override fun processSystem() {

    }

    @Subscribe
    private fun trySpawningPickUp(e: EnemyKilledEvent) {
        if (e.enemyId == -1) return
        val enemyComp = enemyMapper!!.get(e.enemyId)
        val drop = Random.nextFloat()
        if (drop <= enemyComp.pickUpDropChance) {
            //todo: get a pickup based off of the weights map and instantiate the pickup prefab at enemy position
        }
    }
}
