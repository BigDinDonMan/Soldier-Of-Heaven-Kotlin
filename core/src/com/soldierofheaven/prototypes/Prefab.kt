package com.soldierofheaven.prototypes

import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld

abstract class Prefab(private val ecsWorld: EcsWorld, private val physicsWorld: PhysicsWorld) {
    abstract fun instantiate(): Int //returns created entity id
}
