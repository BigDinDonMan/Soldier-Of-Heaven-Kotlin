package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import com.badlogic.gdx.math.Vector2

class ParticleEffect : PooledComponent() {
    var looping = false
    var emittersPositionRef: Vector2? = null
    var particleEffect: com.badlogic.gdx.graphics.g2d.ParticleEffect? = null
    var particleEffectName = ""

    override fun reset() {
        emittersPositionRef = null
        particleEffect = null
        looping = false
        particleEffectName = ""
    }
}
