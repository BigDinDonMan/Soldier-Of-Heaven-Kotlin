package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import com.soldierofheaven.weapons.Weapon

//todo: change interpolated colors to be darker (dark red & dark green, or something else)
class ReloadBar(private val outlineTexture: Texture, private val playerPositionVector: Vector3, private val gameCamera: Camera, private val yOffset: Float) : Actor(), Disposable {

    private val projectionVector = Vector3()
    private val shapeRenderer = ShapeRenderer()
    private var enabled = false
    private var weapon: Weapon? = null
    private var barColor = Color(1f, 0f, 0f, 1f)
    var barWidth: Float
    var barHeight: Float
    var barPaddingX: Float = 0f

    init {
        width = outlineTexture.width.toFloat()
        height = outlineTexture.height.toFloat()
        barWidth = width
        barHeight = height
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (!enabled) return

        projectionVector.set(playerPositionVector)
        gameCamera.project(projectionVector)

        //center the bar above player
        setPosition(projectionVector.x - width / 2, projectionVector.y + yOffset)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (!enabled) return
        barColor.set(Color.RED)

        batch.end()

        val barX = x
        val barY = y + height / 2 - barHeight / 2
        val reloadProgress = weapon?.reloadProgress() ?: 0f
        val color = barColor.lerp(Color.GREEN, reloadProgress)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.rect(barX + barPaddingX, barY, (barWidth - barPaddingX * 2) * reloadProgress, barHeight, color, color, color, color)
        shapeRenderer.end()
        batch.begin()
        batch.draw(outlineTexture, x, y)
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

    fun setBarSize(w: Float, h: Float) {
        barWidth = w
        barHeight = h
    }

    fun setPadding(x: Float, y: Float) {

    }
}
