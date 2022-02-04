package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import com.soldierofheaven.Weapon

//todo: add bar outline texture
//todo: change interpolated colors to be darker (dark red & dark green, or something else)
class ReloadBar(private val playerPositionVector: Vector3, private val gameCamera: Camera, private val yOffset: Float) : Actor(), Disposable {

    private val projectionVector = Vector3()
    private val shapeRenderer = ShapeRenderer()
    private var enabled = false
    private var weapon: Weapon? = null
    private var barColor = Color(1f, 0f, 0f, 1f)

    override fun act(delta: Float) {
        super.act(delta)
        if (!enabled) return

        projectionVector.set(playerPositionVector)
        gameCamera.project(projectionVector)

        //center the bar above player
        setPosition(projectionVector.x - width / 2, projectionVector.y + yOffset)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (!enabled) return
        barColor.set(Color.RED)

        val reloadProgress = weapon?.reloadProgress() ?: 0f

        //side rects' borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        shapeRenderer.end()

        //actual bar border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.rect(x, y - height / 2, width, height, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)
        shapeRenderer.end()

        //actual bar fills
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        val color = barColor.lerp(Color.GREEN, reloadProgress)
        val border = 2
        val barX = x + border / 2
        val barY = y + border / 2 - height / 2
        val baseBarWidth = width - border
        val barHeight = height - border
        shapeRenderer.rect(barX, barY, baseBarWidth , barHeight, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE)
        shapeRenderer.rect(barX, barY, baseBarWidth * reloadProgress, barHeight, color, color, color, color)
        shapeRenderer.end()
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }

    fun setWeapon(w: Weapon) {
        weapon = w
    }

    fun setEnabled(b: Boolean) {
        enabled = b
    }
}
