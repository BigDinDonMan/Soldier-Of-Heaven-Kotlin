package com.soldierofheaven.prototypes.enemies

import com.artemis.ComponentMapper
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.prototypes.Prefab
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld

class ImpPrefab(ecsWorld: EcsWorld,physicsWorld: PhysicsWorld, assetManager: AssetManager) : Prefab(ecsWorld,physicsWorld, assetManager) {
    private val transformMapper: ComponentMapper<Transform> = ecsWorld.getMapper(Transform::class.java)

    override fun instantiate(): Int {
        val id = ecsWorld.create()
        val edit =ecsWorld.edit(id)
        return id
    }

    override fun instantiate(x: Float, y: Float): Int {
        return -1
    }
}
