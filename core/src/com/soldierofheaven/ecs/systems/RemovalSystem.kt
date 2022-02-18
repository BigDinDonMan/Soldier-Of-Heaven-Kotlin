package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.soldierofheaven.ecs.components.LifeCycle
import com.soldierofheaven.ecs.components.ParticleEffect
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.util.PhysicsWorld
import java.util.*

@All(LifeCycle::class)
class RemovalSystem() : IteratingSystem() {

    @Wire(name = "physicsWorld")
    private var physicsWorld: PhysicsWorld? = null

    @Wire
    var lifeCycleMapper: ComponentMapper<LifeCycle>? = null

    @Wire
    var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    @Wire
    var particleEffectMapper: ComponentMapper<ParticleEffect>? = null

    private val removalQueue = LinkedList<Int>()

    override fun process(entityId: Int) {
        val lifeCycle = lifeCycleMapper!!.get(entityId)
        lifeCycle.lifeTime -= world.delta
        if (lifeCycle.lifeTime <= 0f) {
            removalQueue += entityId
        }
    }

    override fun end() {
        removalQueue.forEach { id -> kotlin.run {
            world.delete(id)
            val rigidBody = rigidBodyMapper!!.get(id)
            if (rigidBody?.physicsBody != null) {
                physicsWorld!!.destroyBody(rigidBody.physicsBody)
            }
            val particleEffect = particleEffectMapper!!.get(id)
            if (particleEffect != null) {
                (particleEffect.particleEffect as ParticleEffectPool.PooledEffect).free()
                println("freed!")
            }
        } }
        removalQueue.clear()
    }
}
