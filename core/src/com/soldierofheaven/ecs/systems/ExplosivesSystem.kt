package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.EventQueue
import com.soldierofheaven.Physics
import com.soldierofheaven.ecs.components.Explosive
import com.soldierofheaven.ecs.components.LifeCycle
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.ecs.events.DamageEvent
import com.soldierofheaven.ecs.events.ExplosionEvent
import com.soldierofheaven.ecs.events.KnockbackEvent
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

    private val explosionOverlapArray = IntArray(100)

    override fun process(entityId: Int) {
        val explosive = explosiveMapper!!.get(entityId)
        val lifeCycle = lifeCycleMapper!!.get(entityId)

        explosive.fuseTime -= world.delta
        if (explosive.fuseTime <= 0f) {
            makeExplosion(entityId, explosive)
        }
    }

    private fun makeExplosion(id:Int, e: Explosive) {
    }

    @Subscribe
    private fun overlapExplosion(e: ExplosionEvent) {
        val capturedCount = Physics.overlapSphereNonAlloc(e.centerX, e.centerY, e.range, explosionOverlapArray)
        for (i in 0 until capturedCount) {
            //iterate over captured entities and queue them in damage system
            val entityId = explosionOverlapArray[i]
            //to calculate the direction of knockback simply calculate the direction between an entity and explosion center
            //where destination is enemy position and origin is explosion center
            //and then perform subtraction destination - origin
            EventQueue.dispatch(DamageEvent(entityId, e.damage))
            //todo: calculate direction and strength (strength should somehow depend on the range or should be added in the bullet data)
            EventQueue.dispatch(KnockbackEvent(entityId, 5f, 0f, 0f ))
        }
    }
}
