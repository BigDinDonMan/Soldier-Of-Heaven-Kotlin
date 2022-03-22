package com.soldierofheaven.prototypes.pickups

import com.badlogic.gdx.assets.AssetManager
import com.soldierofheaven.Physics
import com.soldierofheaven.Tags
import com.soldierofheaven.ecs.components.*
import com.soldierofheaven.ecs.components.enums.PickUpType
import com.soldierofheaven.prototypes.Prefab
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld

class PickUpPrefab(ecsWorld: EcsWorld, physicsWorld: PhysicsWorld, assetManager: AssetManager) : Prefab(ecsWorld, physicsWorld, assetManager) {
    /*Prefab params*/
    /*textureName - String OR texture - Texture*/
    /*payload - Int | AmmoInfo*/
    /*pickUpType - PickUpType*/
    override fun instantiate(): Int {
        val id = ecsWorld.create()
        val edit = ecsWorld.edit(id)
        edit.create(Transform::class.java).apply {  }
        edit.create(Tag::class.java).apply { value = Tags.PICKUP }
        edit.create(RigidBody::class.java).apply {
            physicsBody = Physics.newSquareBody(id, 24f, 0f, isSensor = true)
        }
        edit.create(PickUp::class.java).apply {
            pickUpType = prefabParams.get("pickUpType") as PickUpType
            pickUpPayload = prefabParams.get("payload") ?: null //if it is null then set it up in the game
        }
//        edit.create(TextureDisplay::class.java).apply {  }
        return id
    }

    override fun instantiate(x: Float, y: Float): Int {
        val id = this.instantiate()
        rigidBodyMapper.get(id).apply {
            physicsBody!!.setTransform(x, y, physicsBody!!.angle)
        }
        return id
    }
}
