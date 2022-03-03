package com.soldierofheaven.prototypes.pickups

import com.badlogic.gdx.assets.AssetManager
import com.soldierofheaven.ecs.components.PickUp
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Tag
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.ecs.components.enums.PickUpType
import com.soldierofheaven.prototypes.Prefab
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld

class ExplosivesPickUp(ecsWorld: EcsWorld, physicsWorld: PhysicsWorld, assetManager: AssetManager) : Prefab(ecsWorld, physicsWorld, assetManager) {

    private val rigidBodyMapper = ecsWorld.getMapper(RigidBody::class.java)

    override fun instantiate(): Int {
        val id = ecsWorld.create()
        val edit = ecsWorld.edit(id)
        edit.create(Transform::class.java).apply {  }
        edit.create(Tag::class.java).apply {  }
        edit.create(RigidBody::class.java).apply {  }
        edit.create(PickUp::class.java).apply {
            pickUpType = PickUpType.EXPLOSIVES
        }
        return id
    }

    override fun instantiate(x: Float, y: Float): Int {
        val id = this.instantiate()
        val rigidBody = rigidBodyMapper.get(id)
        if (rigidBody?.physicsBody != null) {
            rigidBody.physicsBody!!.setTransform(x, y, rigidBody.physicsBody!!.angle)
        }
        return id
    }
}
