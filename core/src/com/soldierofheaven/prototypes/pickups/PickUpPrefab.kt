package com.soldierofheaven.prototypes.pickups

import com.badlogic.gdx.assets.AssetManager
import com.soldierofheaven.ecs.components.*
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
        edit.create(Tag::class.java).apply {  }
        edit.create(RigidBody::class.java).apply {  }
        edit.create(PickUp::class.java).apply {  }
        edit.create(TextureDisplay::class.java).apply {  }
        return id
    }

    override fun instantiate(x: Float, y: Float): Int {
        val id = this.instantiate()
        ecsWorld.getEntity(id).getComponent(RigidBody::class.java).apply {
            physicsBody!!.setTransform(x, y, physicsBody!!.angle)
        }
        return id
    }
}
