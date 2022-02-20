package com.soldierofheaven

import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld

object Physics {

    private lateinit var physicsWorld: PhysicsWorld
    private lateinit var ecsWorld: EcsWorld

    fun init(physicsWorld: PhysicsWorld, ecsWorld: EcsWorld) {
        this.physicsWorld = physicsWorld
        this.ecsWorld = ecsWorld
    }

    fun overlapSphere(x: Float, y: Float, radius: Float): List<Int> {
        return emptyList()
    }

    //does not allocate a buffer, returns number of captured objects, and needs an output array as a parameter
    fun overlapSphereNonAlloc(x: Float, y: Float, radius: Float, outputArray: IntArray): Int {
        var currentIndex = 0
        return 0
    }
}
