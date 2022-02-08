package com.soldierofheaven

import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool

object ParticlePools {
    private val pools: MutableMap<String, ParticleEffectPool> = HashMap()

    fun registerEffect(effectName: String, effect: ParticleEffect, size: Int, maxSize: Int) {
        pools[effectName]?.clear()
        pools[effectName] = ParticleEffectPool(effect, size, maxSize)
    }

    fun obtain(name: String): ParticleEffectPool.PooledEffect = pools[name]!!.obtain()

    fun free(name: String, effect: ParticleEffectPool.PooledEffect) = pools[name]!!.free(effect)
}
