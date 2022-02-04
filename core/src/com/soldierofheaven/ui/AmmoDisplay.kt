package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.soldierofheaven.Weapon

class AmmoDisplay(private val ammoIcon: Texture, private val skin: Skin) : Actor() {

    private val clipLabel = Label("", skin)
    private val storedAmmoLabel = Label("", skin)

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(ammoIcon, x, y)
        clipLabel.draw(batch, parentAlpha)
        storedAmmoLabel.draw(batch, parentAlpha)
    }

    fun update(weapon: Weapon) {
        clipLabel.setText("${weapon.currentAmmo}/${weapon.clipSize}")
        storedAmmoLabel.setText(if (weapon.maxStoredAmmo == Weapon.INFINITE_AMMO) "∞" else "${weapon.storedAmmo}/${weapon.maxStoredAmmo}")
    }

    override fun positionChanged() {
        val padding = 5f
        val xPos = x + ammoIcon.width + padding
        clipLabel.setPosition(xPos, y + height - clipLabel.height)
        storedAmmoLabel.setPosition(xPos, y)
    }
}
