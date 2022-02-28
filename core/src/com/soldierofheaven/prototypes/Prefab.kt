package com.soldierofheaven.prototypes

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld

abstract class Prefab(protected val ecsWorld: EcsWorld,protected val physicsWorld: PhysicsWorld, protected val assetManager: AssetManager) {
    abstract fun instantiate(): Int //returns created entity id
    abstract fun instantiate(x: Float, y: Float): Int
    fun instantiate(position: Vector2) = instantiate(position.x, position.y)
}
