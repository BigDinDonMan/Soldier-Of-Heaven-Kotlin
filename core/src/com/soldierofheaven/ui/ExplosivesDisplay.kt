package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.soldierofheaven.stats.StatisticsTracker
import kotlin.math.max

class ExplosivesDisplay(explosiveIcon : Texture, skin: Skin) : Actor() {
    private val explosivesLabel = Label(formatExplosives(), skin)
    private val explosivesImage = Image(explosiveIcon)

    override fun act(delta: Float) {
        super.act(delta)
        explosivesImage.setPosition(x, y)
        explosivesLabel.setPosition(x + 10f + explosivesImage.width, y)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        explosivesImage.draw(batch, parentAlpha)
        explosivesLabel.draw(batch, parentAlpha)
    }

    private fun formatExplosives(): String {
        return "${StatisticsTracker.explosives}/${StatisticsTracker.maxExplosives}"
    }

    fun update() {
        explosivesLabel.setText(formatExplosives())
    }

    override fun getWidth(): Float {
        return explosivesImage.width + 10f + explosivesLabel.width
    }

    override fun getHeight(): Float {
        return max(explosivesImage.height, explosivesLabel.height)
    }
}
