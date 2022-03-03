package com.soldierofheaven.ecs.systems

import box2dLight.RayHandler
import com.artemis.annotations.One
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.LightSource
import com.soldierofheaven.util.PhysicsWorld

@One(LightSource::class)
class LightsProcessingSystem(private val physicsWorld: PhysicsWorld) : IteratingSystem() {

    private val rayHandler = RayHandler(physicsWorld)


    override fun process(entityId: Int) {
    }
}
