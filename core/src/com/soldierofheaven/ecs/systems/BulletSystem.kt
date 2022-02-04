package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.Bullet
import com.soldierofheaven.ecs.components.RigidBody

@All(Bullet::class, RigidBody::class)
class BulletSystem : IteratingSystem() {

    @Wire
    var bulletMapper: ComponentMapper<Bullet>? = null

    @Wire
    var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    override fun process(entityId: Int) {
        val bullet = bulletMapper!!.get(entityId)
        val rigidBody = rigidBodyMapper!!.get(entityId)
        if (rigidBody?.physicsBody == null) return

        rigidBody.physicsBody!!.applyLinearImpulse(
            bullet.moveDirection.x * bullet.speed,
            bullet.moveDirection.y * bullet.speed,
            rigidBody.physicsBody!!.position.x,
            rigidBody.physicsBody!!.position.y,
            true
        )
    }
}
