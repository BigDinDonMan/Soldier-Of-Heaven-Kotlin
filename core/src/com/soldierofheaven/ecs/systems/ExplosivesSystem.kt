package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.Explosive
import com.soldierofheaven.ecs.components.LifeCycle
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.ecs.events.ExplosiveCollisionEvent
import com.soldierofheaven.util.PhysicsWorld
import net.mostlyoriginal.api.event.common.Subscribe

@All(Explosive::class)
class ExplosivesSystem : IteratingSystem() {

    @Wire
    var explosiveMapper: ComponentMapper<Explosive>? = null

    @Wire
    var lifeCycleMapper: ComponentMapper<LifeCycle>? = null

    @Wire
    var transformMapper: ComponentMapper<Transform>? = null

    @Wire
    var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    @Wire(name = "physicsWorld")
    var physicsWorld: PhysicsWorld? = null

    override fun process(entityId: Int) {
        val explosive = explosiveMapper!!.get(entityId)
        val lifeCycle = lifeCycleMapper!!.get(entityId)

        explosive.fuseTime -= world.delta
        if (explosive.fuseTime <= 0f) {
            makeExplosion(entityId, explosive)
        }
    }

    @Subscribe
    private fun handleExplosiveCollision(e: ExplosiveCollisionEvent) {
        //i dont yet know how to handle this...
    }

    private fun makeExplosion(id:Int, e: Explosive) {

    }
}
