package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class CurrencyDisplay(skin: Skin) : Actor() {
    private val currencyImage = Image(skin.getDrawable("currency-icon"))
    private val currencyLabel = Label("0", skin)

    override fun act(delta: Float) {
        super.act(delta)
        currencyImage.act(delta)
        currencyLabel.act(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        currencyImage.draw(batch, parentAlpha)
        currencyLabel.draw(batch, parentAlpha)
    }

    override fun setPosition(x: Float, y: Float) {
        super.setPosition(x, y)
        val imageX = x + currencyLabel.width + 5f
        val labelY = currencyImage.height / 2 - currencyLabel.height / 2
        currencyLabel.setPosition(x, labelY)
        currencyImage.setPosition(imageX, y)
    }
}
