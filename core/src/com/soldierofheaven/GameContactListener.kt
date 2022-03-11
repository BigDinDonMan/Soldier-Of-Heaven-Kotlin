package com.soldierofheaven

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.soldierofheaven.ecs.components.*
import com.soldierofheaven.ecs.events.DamageEvent
import com.soldierofheaven.ecs.events.ExplosionEvent
import com.soldierofheaven.ecs.events.KnockbackEvent
import com.soldierofheaven.ecs.events.PickUpEvent
import com.soldierofheaven.util.EcsWorld

class GameContactListener(private val ecsWorld: EcsWorld) : ContactListener {

    private val tagMapper = ecsWorld.getMapper(Tag::class.java)
    private val bulletMapper = ecsWorld.getMapper(Bullet::class.java)
    private val lifeCycleMapper = ecsWorld.getMapper(LifeCycle::class.java)
    private val damageMapper = ecsWorld.getMapper(Damage::class.java)
    private val pickUpMapper = ecsWorld.getMapper(PickUp::class.java)
    private val rigidBodyMapper = ecsWorld.getMapper(RigidBody::class.java)
    private val contactDamageMapper = ecsWorld.getMapper(ContactDamage::class.java)
    private val enemyMapper = ecsWorld.getMapper(Enemy::class.java)


    private val calculationVector = Vector2()

    //this function is a huge stinker but I don't think there is any other way to handle collisions unless I store callbacks in entities
    //and i'd like to avoid that
    override fun beginContact(contact: Contact) {
        val entityAId = contact.fixtureA.body.userData as Int
        val entityBId = contact.fixtureB.body.userData as Int
        val tagA = tagMapper.get(entityAId)
        val tagB = tagMapper.get(entityBId)

        if (tagA.value == Tags.BULLET) {
            handleBulletCollisionWithEntity(contact, entityAId, entityBId, tagB.value)
        } else if (tagB.value == Tags.BULLET) {
            handleBulletCollisionWithEntity(contact, entityBId, entityAId, tagA.value)
        }

        if (tagA.value == Tags.PICKUP && tagB.value == Tags.PLAYER) {
            handlePickUp(contact, entityAId, entityBId)
        } else if (tagB.value == Tags.PICKUP && tagA.value == Tags.PLAYER) {
            handlePickUp(contact, entityBId, entityAId)
        }

        if (tagA.value == Tags.ENEMY && tagB.value == Tags.PLAYER) {
            handleEnemyContactWithPlayer(contact, entityBId, entityAId)
        } else if (tagA.value == Tags.PLAYER && tagB.value == Tags.ENEMY){
            handleEnemyContactWithPlayer(contact, entityAId, entityBId)
        }
    }

    override fun endContact(contact: Contact) {
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
    }

    private fun handleBulletCollisionWithEntity(contact: Contact, bulletId: Int, entityId: Int, entityTag: String) {
        val bullet = bulletMapper[bulletId]
        val lifeCycle = lifeCycleMapper[bulletId]
        val damage = damageMapper[bulletId]
        if (entityTag in damage.damageableTags) {
            lifeCycle.lifeTime = -1f

            if (bullet.isExplosive()) {
                val bulletBody = contact.fixtureA.body
                EventQueue.dispatch(ExplosionEvent(bulletBody.position.x, bulletBody.position.y, damage.value, bullet.explosionRange!!, bullet.explosionStrength!!))
            } else {
                EventQueue.dispatch(DamageEvent(entityId, damage.value))
            }
        }
    }

    private fun handlePickUp(contact: Contact, pickUpId: Int, playerId: Int) {
        val pickUp = pickUpMapper.get(pickUpId)
        ecsWorld.edit(pickUpId).create(LifeCycle::class.java).apply { lifeTime = -1f }
        EventQueue.dispatch(PickUpEvent(pickUp))
    }

    private fun handleEnemyContactWithPlayer(contact: Contact, playerId: Int, enemyId: Int) {
        val enemyComp = enemyMapper.get(enemyId)
        val enemyDamage = contactDamageMapper.get(enemyId) ?: return
        val enemyRigidBody = rigidBodyMapper.get(enemyId)
        if (enemyRigidBody?.physicsBody == null) return

        calculationVector.set(enemyComp.playerPositionRef!!).sub(enemyRigidBody.physicsBody!!.position)

        EventQueue.dispatchMultiple(
            DamageEvent(playerId, enemyDamage.value),
            KnockbackEvent(playerId, enemyDamage.knockback, calculationVector.x, calculationVector.y)
        )
    }
}
