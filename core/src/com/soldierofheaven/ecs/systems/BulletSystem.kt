package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.Bullet
import com.soldierofheaven.ecs.components.Explosive
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Speed

@All(Bullet::class, RigidBody::class, Speed::class)
class BulletSystem : IteratingSystem() {

    @Wire
    var bulletMapper: ComponentMapper<Bullet>? = null

    @Wire
    var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    @Wire
    var explosiveMapper: ComponentMapper<Explosive>? = null

    @Wire
    var speedMapper: ComponentMapper<Speed>? = null

    override fun process(entityId: Int) {
        val bullet = bulletMapper!!.get(entityId)
        val rigidBody = rigidBodyMapper!!.get(entityId)
        val speed = speedMapper!!.get(entityId)
        if (rigidBody?.physicsBody == null) return

        rigidBody.physicsBody!!.setLinearVelocity(
            bullet.moveDirection.x * speed.value,
            bullet.moveDirection.y * speed.value
        )
    }
}
