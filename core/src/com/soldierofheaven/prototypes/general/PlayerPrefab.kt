package com.soldierofheaven.prototypes.general

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.soldierofheaven.Physics
import com.soldierofheaven.Tags
import com.soldierofheaven.ecs.components.*
import com.soldierofheaven.prototypes.Prefab
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld

class PlayerPrefab(ecsWorld: EcsWorld,physicsWorld: PhysicsWorld, assetManager: AssetManager) : Prefab(ecsWorld, physicsWorld, assetManager) {
    override fun instantiate(): Int {
        val id =ecsWorld.create()
        val edit = ecsWorld.edit(id)
        val playerSize = 48f
        edit.create(Transform::class.java)
        edit.create(RigidBody::class.java).apply {
            physicsBody = Physics.newSquareBody(id, playerSize, gravityScale = 0f, linearDamping = 5f, friction = 2f)
        }
        edit.create(Player::class.java)
        edit.create(Tag::class.java).apply { value = Tags.PLAYER }
        edit.create(Health::class.java)
        edit.create(Speed::class.java).apply { value = 25f }
        return id
    }

    override fun instantiate(x: Float, y: Float): Int {
        val id = this.instantiate()
        rigidBodyMapper.get(id).apply { physicsBody!!.setTransform(x,y,physicsBody!!.angle) }
        return id
    }
}
