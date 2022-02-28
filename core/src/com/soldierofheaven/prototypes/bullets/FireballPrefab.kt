package com.soldierofheaven.prototypes.bullets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.soldierofheaven.ecs.components.*
import com.soldierofheaven.prototypes.Prefab
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld

class FireballPrefab(ecsWorld: EcsWorld, physicsWorld: PhysicsWorld, assetManager: AssetManager) : Prefab(ecsWorld, physicsWorld, assetManager) {
    override fun instantiate(): Int {
        val id = ecsWorld.create()
        val edit = ecsWorld.edit(id)
        edit.create(Damage::class.java).apply {  }
        edit.create(Speed::class.java).apply {  }
        edit.create(Transform::class.java).apply {  }
        edit.create(TextureDisplay::class.java).apply {  }
        edit.create(RigidBody::class.java).apply {  }
        return id
    }

    override fun instantiate(x: Float, y: Float): Int {
        return -1
    }
}
