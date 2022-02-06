package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.util.PhysicsWorld

@All(Transform::class, RigidBody::class)
class PhysicsSystem(private val physicsWorld: PhysicsWorld, private val velocityIterations: Int = 6, private val positionIterations: Int = 2) : IteratingSystem() {

    val TIME_STEP = 1 / 660f //dont ask; bullets behave the best at this time step, with 12/12 iterations
    var accumulator = 0f

    @Wire
    var transformMapper: ComponentMapper<Transform>? = null
    @Wire
    var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    override fun begin() {
        val delta = world.delta
        accumulator += delta
        while (accumulator >= delta) {
            physicsWorld.step(TIME_STEP, velocityIterations, positionIterations)
            accumulator -= TIME_STEP
        }
    }

    override fun process(entityId: Int) {
        val entityRigidBody = rigidBodyMapper!!.get(entityId)
        val entityTransform = transformMapper!!.get(entityId)

        if (entityRigidBody.physicsBody == null) return

        entityTransform.position.set(
            entityRigidBody.physicsBody!!.position.x - entityTransform.size.x / 2,
            entityRigidBody.physicsBody!!.position.y - entityTransform.size.y / 2,
            entityTransform.position.z
        )
    }
}
