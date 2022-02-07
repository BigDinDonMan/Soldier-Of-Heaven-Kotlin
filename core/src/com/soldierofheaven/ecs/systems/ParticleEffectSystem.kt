package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.soldierofheaven.ecs.components.ParticleEffect
import com.soldierofheaven.ecs.components.Transform

@All(ParticleEffect::class, Transform::class)
class ParticleEffectSystem(private val spriteBatch: SpriteBatch, private val gameCamera: Camera): IteratingSystem() {
    @Wire
    var particleMapper: ComponentMapper<ParticleEffect>? = null

    @Wire
    var transformMapper: ComponentMapper<Transform>? = null

    override fun begin() {
        spriteBatch.projectionMatrix = gameCamera.combined
        spriteBatch.begin()
    }

    override fun process(entityId: Int) {
        val particleEffectComp = particleMapper!!.get(entityId)
        val transform = transformMapper!!.get(entityId)

        if (particleEffectComp.particleEffect == null) return
        particleEffectComp.particleEffect?.update(world.delta)
        particleEffectComp.particleEffect?.setPosition(transform.position.x, transform.position.y)
        particleEffectComp.particleEffect?.emitters?.forEach { e -> kotlin.run {
            e.angle.setLow(transform.rotation)
            e.angle.setHigh(transform.rotation)
        } }
        if (particleEffectComp.particleEffect!!.isComplete && particleEffectComp.looping) {
            particleEffectComp.particleEffect!!.start()
        }

        particleEffectComp.particleEffect!!.draw(spriteBatch, world.delta)
    }

    override fun end() {
        spriteBatch.end()
    }
}
