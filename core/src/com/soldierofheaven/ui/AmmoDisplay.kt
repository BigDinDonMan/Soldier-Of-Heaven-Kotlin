package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.soldierofheaven.weapons.Weapon
import kotlin.math.max

//ammo icons need to be the same size
class AmmoDisplay(private var ammoIcon: Texture, skin: Skin) : Actor() {

    companion object {
        const val INFINITE_AMMO_STR = "N/A"//'\u221e'.toString()
    }

    private val clipLabel = Label("", skin)
    private val storedAmmoLabel = Label("", skin)

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(ammoIcon, x, y)
        clipLabel.draw(batch, parentAlpha)
        storedAmmoLabel.draw(batch, parentAlpha)
    }

    override fun act(delta: Float) {
        super.act(delta)
        val padding = 10f
        val labelsSpacing = 7.5f
        val xPos = x + ammoIcon.width + padding
        storedAmmoLabel.setPosition(xPos, y + labelsSpacing)
        clipLabel.setPosition(xPos, y + ammoIcon.height - labelsSpacing)
    }

    override fun getWidth(): Float {
        return ammoIcon.width + max(clipLabel.width, storedAmmoLabel.width) + 10f
    }

    override fun getHeight(): Float {
        return ammoIcon.height.toFloat()
    }

    fun update(weapon: Weapon) {
        clipLabel.setText("${weapon.currentAmmo}/${weapon.clipSize}")
        storedAmmoLabel.setText(if (weapon.maxStoredAmmo == Weapon.INFINITE_AMMO) INFINITE_AMMO_STR else "${weapon.storedAmmo}/${weapon.maxStoredAmmo}")
        ammoIcon = weapon.ammoIcon
    }
}
