package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.One
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.*

@All(RigidBody::class, Speed::class)
class BulletSystem : IteratingSystem() {

    @Wire
    var bulletMapper: ComponentMapper<Bullet>? = null

    @Wire
    var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    @Wire
    var speedMapper: ComponentMapper<Speed>? = null

    override fun process(entityId: Int) {
        val bullet = bulletMapper!!.get(entityId)
        val rigidBody = rigidBodyMapper!!.get(entityId)
        val speed = speedMapper!!.get(entityId)
        if (rigidBody?.physicsBody == null) return

        if (bullet != null) {
            rigidBody.physicsBody!!.setLinearVelocity(
                bullet.moveDirection.x * speed.value,
                bullet.moveDirection.y * speed.value
            )
        }
    }
}
