package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.soldierofheaven.EventQueue
import com.soldierofheaven.Physics
import com.soldierofheaven.ecs.components.*
import com.soldierofheaven.ecs.events.DamageEvent
import com.soldierofheaven.ecs.events.ExplosionEvent
import com.soldierofheaven.ecs.events.KnockbackEvent
import com.soldierofheaven.util.PhysicsWorld
import com.soldierofheaven.util.math.cbrt
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.math.sqrt

@All(Explosive::class)
class ExplosivesSystem : IteratingSystem() {

    @Wire
    var explosiveMapper: ComponentMapper<Explosive>? = null

    @Wire
    var lifeCycleMapper: ComponentMapper<LifeCycle>? = null

    @Wire
    var transformMapper: ComponentMapper<Transform>? = null

    @Wire
    var damageMapper: ComponentMapper<Damage>? = null

    @Wire
    var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    @Wire(name = "physicsWorld")
    var physicsWorld: PhysicsWorld? = null

    private val explosionOverlapArray = IntArray(100)
    private val calculationVector = Vector2()

    override fun process(entityId: Int) {
        val explosive = explosiveMapper!!.get(entityId)
        val lifeCycle = lifeCycleMapper!!.get(entityId)
        val rigidBody = rigidBodyMapper!!.get(entityId)
        val damage = damageMapper!!.get(entityId)

        explosive.fuseTime -= world.delta
        if (explosive.fuseTime <= 0f) {
            //queue event
            lifeCycle.lifeTime = -1f
            if (rigidBody?.physicsBody != null)
                EventQueue.dispatch(ExplosionEvent(
                    rigidBody.physicsBody!!.position.x, rigidBody.physicsBody!!.position.y,
                    damage.value, explosive.range, explosive.strength)
                )
        }
    }

    @Subscribe
    private fun overlapExplosion(e: ExplosionEvent) {
        val capturedCount = Physics.overlapSphereNonAlloc(e.centerX, e.centerY, e.range, explosionOverlapArray)
        for (i in 0 until capturedCount) {
            //iterate over captured entities and queue them in damage system
            val entityId = explosionOverlapArray[i]
            val rigidBody = rigidBodyMapper!!.get(entityId)
            if (rigidBody?.physicsBody == null) continue

            //direction needs to be normalized because knockback strength is not constant when not normalized
            val entityPosition = rigidBody.physicsBody!!.position
            calculationVector.set(entityPosition.x - e.centerX, entityPosition.y - e.centerY)
            calculationVector.nor()

            EventQueue.dispatchMultiple(
                DamageEvent(entityId, e.damage),
                KnockbackEvent(entityId, e.strength, calculationVector.x, calculationVector.y)
            )
        }
    }
}
