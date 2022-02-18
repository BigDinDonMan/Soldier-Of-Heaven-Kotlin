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
class ParticleEffectSystem(): IteratingSystem() {
    @Wire
    private var particleMapper: ComponentMapper<ParticleEffect>? = null

    @Wire
    private var transformMapper: ComponentMapper<Transform>? = null

    @Wire(name = "mainBatch")
    private var spriteBatch: SpriteBatch? = null

    @Wire(name = "gameCamera")
    private var gameCamera: Camera? = null

    override fun begin() {
        spriteBatch!!.projectionMatrix = gameCamera!!.combined
        spriteBatch!!.begin()
    }

    override fun process(entityId: Int) {
        val particleEffectComp = particleMapper!!.get(entityId)
        val transform = transformMapper!!.get(entityId)

        if (particleEffectComp.particleEffect == null) return
        if (particleEffectComp.emittersPositionRef != null) {
            //its a dynamic one (e.g. rocket trail) so we assign the possition to vector reference
            particleEffectComp.particleEffect!!.setPosition(particleEffectComp.emittersPositionRef!!.x, particleEffectComp.emittersPositionRef!!.y)
        } else {
            //its a stationary particle effect
            particleEffectComp.particleEffect?.setPosition(transform.position.x, transform.position.y)
        }
        particleEffectComp.particleEffect?.update(world.delta)
        //fixme: this does not work properly :/
//        particleEffectComp.particleEffect?.emitters?.forEach { e -> kotlin.run {
//            e.angle.setLow(transform.rotation)
//            e.angle.setHigh(transform.rotation)
//        } }
        if (particleEffectComp.particleEffect!!.isComplete && particleEffectComp.looping) {
            particleEffectComp.particleEffect!!.start()
        }

        particleEffectComp.particleEffect!!.draw(spriteBatch, world.delta)
    }

    override fun end() {
        spriteBatch!!.end()
    }
}
