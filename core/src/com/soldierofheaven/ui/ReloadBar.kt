package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import com.soldierofheaven.Weapon

class ReloadBar(private val playerPositionVector: Vector3, private val gameCamera: Camera, private val yOffset: Float) : Actor(), Disposable {

    private val projectionVector = Vector3()
    private val shapeRenderer = ShapeRenderer()
    private var enabled = false
    private var weapon: Weapon? = null
    private var barColor = Color(1f, 0f, 0f, 1f)

    override fun act(delta: Float) {
        super.act(delta)
        if (!enabled) return
        println(enabled)

        projectionVector.set(playerPositionVector)
        gameCamera.project(projectionVector)

        //center the bar above player
        setPosition(projectionVector.x - width / 2, projectionVector.y + yOffset)
        println("$x, $y")
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (!enabled) return
        barColor.set(Color.RED)

        val reloadProgress = weapon?.reloadProgress() ?: 0f
        val sideRectSizeX = 4f
        val sideRectSizeY = 13f //dont ask

        //side rects' borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        shapeRenderer.end()

        //side rects fill
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.rect(x, y, sideRectSizeX, sideRectSizeY, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE)
        shapeRenderer.rect(x + width - sideRectSizeX, y, sideRectSizeX, sideRectSizeY, Color.WHITE, Color.WHITE, Color.WHITE,Color.WHITE)
        shapeRenderer.end()

        //actual bar border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.rect(x + sideRectSizeX / 2, y + sideRectSizeY / 2, width, height, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)
        shapeRenderer.end()

        //actual bar fills
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        val color = barColor.lerp(Color.GREEN, reloadProgress)
        val border = 2
        shapeRenderer.rect(x + sideRectSizeX / 2 + border, y + sideRectSizeY + border, (width - border) * reloadProgress, height - border, color, color, color, color)
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
