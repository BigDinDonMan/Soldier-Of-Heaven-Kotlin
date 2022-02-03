package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.soldierofheaven.ecs.components.TextureDisplay
import com.soldierofheaven.ecs.components.Transform
import java.util.*

@All(Transform::class, TextureDisplay::class)
class RenderSystem(val spriteBatch: SpriteBatch, private val gameCamera: Camera) : IteratingSystem() {

    @Wire
    var transformMapper: ComponentMapper<Transform>? = null
    @Wire
    var textureDisplayMapper: ComponentMapper<TextureDisplay>? = null

    private val queue = PriorityQueue<Int> { id1, id2 -> kotlin.run {
        val t1 = transformMapper!!.get(id1)
        val t2 = transformMapper!!.get(id2)
        t1.position.z.compareTo(t2.position.z)
    } }

    override fun begin() {
        queue.clear()
        gameCamera.update()
        spriteBatch.projectionMatrix = gameCamera.combined
        spriteBatch.begin()
    }

    override fun process(entityId: Int) {
        queue.add(entityId)
    }

    override fun end() {
        queue.forEach { id ->
            kotlin.run {
                val transform = transformMapper!!.get(id)
                val textureDisplay = textureDisplayMapper!!.get(id)
                val center = transform.center
                spriteBatch.draw(
                    textureDisplay.region, transform.position.x, transform.position.y,
                    center.x, center.y, transform.size.x, transform.size.y,
                    transform.scale.x, transform.scale.y, transform.rotation
                )
            }
        }

        spriteBatch.end()
    }
}
