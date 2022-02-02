package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.util.PhysicsWorld

@All(Transform::class, RigidBody::class)
class PhysicsSystem(private val physicsWorld: PhysicsWorld, private val velocityIterations: Int = 6, private val positionIterations: Int = 2) : IteratingSystem() {

    val TIME_STEP = 1 / 300f
    var accumulator = 0f

    var transformMapper: ComponentMapper<Transform>? = null
    var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    override fun begin() {
        val delta = Gdx.graphics.deltaTime
        accumulator += delta
        while (accumulator >= delta) {
            physicsWorld.step(TIME_STEP, velocityIterations, positionIterations)
            accumulator -= TIME_STEP
        }
    }

    override fun process(entityId: Int) {

    }
}
