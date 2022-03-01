package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.soldierofheaven.util.`interface`.Resettable
import com.soldierofheaven.util.formatWithThousandsSeparator

class CurrencyDisplay(private val icon: Texture, skin: Skin) : HorizontalGroup(), Resettable {
    private val currencyImage = Image(icon)
    private val currencyLabel = Label("0$", skin)

    init {
        currencyImage.pack()
        currencyLabel.pack()
    }

    constructor(icon: Texture, iconWidth: Float, iconHeight: Float, skin: Skin) : this(icon, skin) {
        currencyImage.setSize(iconWidth, iconHeight)
        currencyImage.pack()
        currencyLabel.pack()
    }

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
        val labelY = y + currencyImage.height / 2 - currencyLabel.height / 2
        currencyLabel.setPosition(x, labelY)
        currencyImage.setPosition(imageX, y)
    }

    fun update(currency: Int) {
        currencyLabel.setText("${formatWithThousandsSeparator(currency)}$")
        invalidateCurrency()
    }

    override fun getHeight(): Float {
        return currencyLabel.height + currencyImage.height
    }

    override fun getWidth(): Float {
        return currencyLabel.width + currencyImage.width + 5f
    }

    private fun invalidateCurrency() {
        currencyLabel.pack()
        val labelX = currencyImage.x - currencyLabel.width - 5f
        currencyLabel.x = labelX
    }

    override fun reset() {
        currencyLabel.setText("0$")
        invalidateCurrency()
    }
}
