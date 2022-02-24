package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.soldierofheaven.util.`interface`.Resettable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ScoreDisplay(skin: Skin) : Actor(), Resettable {
    private val scoreLabel = Label("Score: 0", skin)

    init {
    }

    override fun act(delta: Float) {
        super.act(delta)
        scoreLabel.act(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        scoreLabel.draw(batch,parentAlpha)
    }

    override fun setPosition(x: Float, y: Float) {
        super.setPosition(x, y)
        scoreLabel.setPosition(x, y)
    }

    fun update(score: Int) {
        scoreLabel.setText("Score: ${String.format("%,d", score)}")
    }

    override fun reset() {
        scoreLabel.setText("Score: 0")
    }

    override fun getWidth(): Float {
        return scoreLabel.width
    }

    override fun getHeight(): Float {
        return scoreLabel.height
    }

    override fun setHeight(height: Float) {
        scoreLabel.height = height
    }

    override fun setWidth(width: Float) {
        scoreLabel.width = width
    }

    override fun setSize(width: Float, height: Float) {
        setWidth(width)
        setHeight(height)
    }

    override fun getX(): Float {
        return scoreLabel.x
    }

    override fun getY(): Float {
        return scoreLabel.y
    }
}
