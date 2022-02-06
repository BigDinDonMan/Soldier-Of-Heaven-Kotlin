package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.ParticleEffect

@All(ParticleEffect::class)
class ParticleEffectSystem: IteratingSystem() {

    @Wire
    var particleMapper: ComponentMapper<ParticleEffect>? = null

    override fun process(entityId: Int) {
    }
}
