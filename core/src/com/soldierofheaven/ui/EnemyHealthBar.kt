package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import com.soldierofheaven.ecs.components.Health
import com.soldierofheaven.util.roundedRect

//note: enemyId will be a key by which we will be searching through the health bars to remove them when enemy dies
class EnemyHealthBar(val enemyId: Int, val enemyPositionRef: Vector2, val enemyHealthRef: Health, val yOffset: Float = 30f, val barWidth: Float = 100f, val barHeight: Float = 10f, val barRounding: Float = 7.5f) : Actor(), Disposable {

    private val shapeRenderer = ShapeRenderer()

    override fun dispose() {
        shapeRenderer.dispose()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.end()
        shapeRenderer.color = Color.RED
        shapeRenderer.projectionMatrix = stage.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        val drawX = enemyPositionRef.x - barWidth / 2
        val drawY = enemyPositionRef.y + yOffset
        val healthRatio = enemyHealthRef.health / enemyHealthRef.maxHealth
        shapeRenderer.roundedRect(drawX, drawY, barWidth * healthRatio, barHeight, barRounding)

        shapeRenderer.end()
        batch.begin()
    }
}
