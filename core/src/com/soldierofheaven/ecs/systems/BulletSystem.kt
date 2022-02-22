package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.One
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.soldierofheaven.EventQueue
import com.soldierofheaven.ecs.components.*
import com.soldierofheaven.ecs.components.enums.ExplosiveType
import com.soldierofheaven.ecs.events.ExplosionEvent
import kotlin.math.pow

@All(RigidBody::class, Speed::class, Bullet::class)
class BulletSystem : IteratingSystem() {

    @Wire
    var bulletMapper: ComponentMapper<Bullet>? = null

    @Wire
    var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    @Wire
    var speedMapper: ComponentMapper<Speed>? = null

    @Wire
    var damageMapper: ComponentMapper<Damage>? = null

    companion object {
        const val MISSILE_SPEED_SCALE_POWER = 1.5f
    }

    override fun process(entityId: Int) {
        val bullet = bulletMapper!!.get(entityId)
        val rigidBody = rigidBodyMapper!!.get(entityId)
        val speed = speedMapper!!.get(entityId)
        val damage = damageMapper!!.get(entityId)
        if (rigidBody?.physicsBody == null) return

        if (bullet.isExplosive()) {
            if (bullet.explosionTimer != null && bullet.explosionTimer!! > 0f) {
                val current = bullet.explosionTimer!!
                bullet.explosionTimer = current - world.delta
            } else {
                EventQueue.dispatch(ExplosionEvent(
                    rigidBody.physicsBody!!.position.x,
                    rigidBody.physicsBody!!.position.y,
                    damage.value, bullet.explosionRange!!,
                    bullet.explosionStrength!!
                ))
                world.edit(entityId).create(LifeCycle::class.java).apply { lifeTime = -1f }
            }
            when (bullet.explosiveType) {
                ExplosiveType.GRENADE -> {
                    rigidBody.physicsBody!!.linearDamping = bullet.bulletDamping
                    //this condition might bite me in the butt later but it works so hey
                    if (rigidBody.physicsBody!!.linearVelocity == Vector2.Zero) {
                        val forceX = bullet.moveDirection.x * speed.value
                        val forceY = bullet.moveDirection.y * speed.value
                        rigidBody.physicsBody!!.applyLinearImpulse(
                            forceX, forceY,
                            rigidBody.physicsBody!!.position.x,
                            rigidBody.physicsBody!!.position.y,
                            true
                        )
                    }
                }
                ExplosiveType.MISSILE -> {
                    val mult = speed.value.pow(MISSILE_SPEED_SCALE_POWER)
                    rigidBody.physicsBody!!.applyForceToCenter(
                        bullet.moveDirection.x * mult,
                        bullet.moveDirection.y * mult,
                        true
                    )
                }
            }
        } else {
            rigidBody.physicsBody!!.setLinearVelocity(
                bullet.moveDirection.x * speed.value,
                bullet.moveDirection.y * speed.value
            )
        }

    }
}
