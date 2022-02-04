package com.soldierofheaven.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable

class HealthBar(private val maxHealth: Int, labelSkin: Skin) : Actor(), Disposable {
    private var displayValue = maxHealth
        set(value){
            field = value
            label.setText("${displayValue}/${maxHealth}")
            widthMultiplier = displayValue.toFloat() / maxHealth
        }
    private val label = Label("${displayValue}/${maxHealth}", labelSkin)
    private val shapeRenderer = ShapeRenderer()

    private var widthMultiplier: Float = displayValue.toFloat() / maxHealth

    var borderWidth = 2

    fun updateDisplay(newHealth: Int) {
        displayValue = newHealth
    }

    override fun act(delta: Float) {
        super.act(delta)
        label.act(delta)
        val midX = (x + width) / 2
        val lx = midX - label.width / 2
        val ly = y + height / 2 - label.height / 2
        label.setPosition(lx, ly)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        Gdx.gl.glLineWidth(borderWidth.toFloat())
        batch.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.rect(x, y, width, height, Color.RED, Color.RED, Color.RED, Color.RED)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.rect((x + borderWidth / 2), y + borderWidth / 2, (width - borderWidth) * widthMultiplier, height - borderWidth, Color.RED, Color.GREEN, Color.GREEN, Color.RED)
        shapeRenderer.end()
        batch.begin()
        label.draw(batch, parentAlpha)
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }
}
