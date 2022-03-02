package com.soldierofheaven.ecs.systems

import com.artemis.annotations.One
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.LightSource

@One(LightSource::class)
class LightsProcessingSystem : IteratingSystem() {
    override fun process(entityId: Int) {

    }
}
