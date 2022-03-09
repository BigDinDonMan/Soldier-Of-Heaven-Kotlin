package com.soldierofheaven

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.soldierofheaven.ecs.components.*
import com.soldierofheaven.ecs.events.DamageEvent
import com.soldierofheaven.ecs.events.ExplosionEvent
import com.soldierofheaven.ecs.events.PickUpEvent
import com.soldierofheaven.util.EcsWorld

class GameContactListener(private val ecsWorld: EcsWorld) : ContactListener {

    private val tagMapper = ecsWorld.getMapper(Tag::class.java)
    private val bulletMapper = ecsWorld.getMapper(Bullet::class.java)
    private val lifeCycleMapper = ecsWorld.getMapper(LifeCycle::class.java)
    private val damageMapper = ecsWorld.getMapper(Damage::class.java)
    private val pickUpMapper = ecsWorld.getMapper(PickUp::class.java)

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
}
