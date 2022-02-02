package com.soldierofheaven.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.soldierofheaven.util.heightF

class Crosshair(private val crosshairTexture: Texture) : Actor() {

    fun disable() {

    }

    override fun act(delta: Float) {
        super.act(delta)
        setPosition(
            Gdx.input.x.toFloat() - crosshairTexture.width / 2,
            Gdx.graphics.heightF() - Gdx.input.y.toFloat() - crosshairTexture.height / 2)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        batch.draw(crosshairTexture, x, y)
    }
}
