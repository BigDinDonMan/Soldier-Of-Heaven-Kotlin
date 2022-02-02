package com.soldierofheaven.ecs.systems

import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.soldierofheaven.ecs.components.TextureDisplay
import com.soldierofheaven.ecs.components.Transform

@All(Transform::class, TextureDisplay::class)
class RenderSystem(private val spriteBatch: SpriteBatch) : IteratingSystem() {

    override fun begin() {
        super.begin()
    }

    override fun process(entityId: Int) {

    }

    override fun end() {
        super.end()
    }
}
