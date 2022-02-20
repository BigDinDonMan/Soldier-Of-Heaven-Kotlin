package com.soldierofheaven

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.soldierofheaven.ecs.components.Bullet
import com.soldierofheaven.ecs.components.Damage
import com.soldierofheaven.ecs.components.LifeCycle
import com.soldierofheaven.ecs.components.Tag
import com.soldierofheaven.ecs.events.DamageEvent
import com.soldierofheaven.ecs.events.ExplosionEvent
import com.soldierofheaven.util.EcsWorld

class GameContactListener(private val ecsWorld: EcsWorld) : ContactListener {

    private val tagMapper = ecsWorld.getMapper(Tag::class.java)
    private val bulletMapper = ecsWorld.getMapper(Bullet::class.java)
    private val lifeCycleMapper = ecsWorld.getMapper(LifeCycle::class.java)
    private val damageMapper = ecsWorld.getMapper(Damage::class.java)

    override fun beginContact(contact: Contact) {
        val entityAId = contact.fixtureA.body.userData as Int
        val entityBId = contact.fixtureB.body.userData as Int
        val tagA = tagMapper.get(entityAId)
        val tagB = tagMapper.get(entityBId)
        if (tagA.value == Tags.BULLET) {
            val bullet = bulletMapper[entityAId]
            val lifeCycle = lifeCycleMapper[entityAId]
            val damage = damageMapper[entityAId]
            if (tagB.value in damage.damageableTags) {
                lifeCycle.lifeTime = -1f

                if (bullet.isExplosive()) {
                    val bulletBody = contact.fixtureA.body
                    EventQueue.dispatch(ExplosionEvent(bulletBody.position.x, bulletBody.position.y))
                } else {
                    EventQueue.dispatch(DamageEvent(entityBId, damage.value))
                }
            }
        } else if (tagB.value == Tags.BULLET) {
            val bullet = bulletMapper[entityBId]
            val lifeCycle = lifeCycleMapper[entityBId]
            val damage = damageMapper[entityBId]
            if (tagA.value in damage.damageableTags) {
                lifeCycle.lifeTime = -1f

                if (bullet.isExplosive()) {
                    val bulletBody = contact.fixtureA.body
                    EventQueue.dispatch(ExplosionEvent(bulletBody.position.x, bulletBody.position.y))
                } else {
                    EventQueue.dispatch(DamageEvent(entityAId, damage.value))
                }
            }
        }
    }

    override fun endContact(contact: Contact) {
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
    }
}
