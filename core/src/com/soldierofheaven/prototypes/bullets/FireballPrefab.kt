package com.soldierofheaven.prototypes.bullets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.soldierofheaven.Resources
import com.soldierofheaven.Tags
import com.soldierofheaven.ecs.components.*
import com.soldierofheaven.ecs.components.enums.ExplosiveType
import com.soldierofheaven.prototypes.Prefab
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld

class FireballPrefab(ecsWorld: EcsWorld, physicsWorld: PhysicsWorld, assetManager: AssetManager) : Prefab(ecsWorld, physicsWorld, assetManager) {
    override fun instantiate(): Int {
        val id = ecsWorld.create()
        val edit = ecsWorld.edit(id)
        edit.create(Damage::class.java).apply {
            value = 20f
            damageableTags.add(Tags.PLAYER)
        }
        edit.create(Speed::class.java).apply { value = 250f }
        edit.create(TextureDisplay::class.java).apply {  texture = assetManager.get(Resources.BASIC_BULLET) }
        edit.create(Transform::class.java).apply {
            val tex = assetManager.get(Resources.BASIC_BULLET, Texture::class.java)
            size.set(tex.width.toFloat(), tex.height.toFloat())
        }
        edit.create(RigidBody::class.java).apply {
            val bodyDef = BodyDef().apply {
                gravityScale = 0f
                linearDamping = 0f
                type = BodyDef.BodyType.DynamicBody
                bullet = true
            }
            val shape = CircleShape().apply { radius = assetManager.get(Resources.BASIC_BULLET, Texture::class.java).width.toFloat() / 2 }
            val fixtureDef = FixtureDef().apply {
                this.shape = shape
                isSensor = true
            }

            val body = physicsWorld.createBody(bodyDef).apply {
                createFixture(fixtureDef)
                userData = id
            }

            physicsBody = body

            shape.dispose()
        }
        edit.create(Tag::class.java).apply { value = Tags.BULLET }
        edit.create(Bullet::class.java).apply {
            explosiveType = ExplosiveType.GENERIC
            explosionRange = 100f
            explosionStrength = 250f
            explodeOnContact = true
            bulletDamping = 0f
        }
        edit.create(LifeCycle::class.java).apply { lifeTime = 7.5f }
        return id
    }

    override fun instantiate(x: Float, y: Float): Int {
        val id = instantiate()
        val rigidBody = rigidBodyMapper.get(id)
        rigidBody.physicsBody!!.setTransform(x, y, rigidBody.physicsBody!!.angle)
        return id
    }
}
