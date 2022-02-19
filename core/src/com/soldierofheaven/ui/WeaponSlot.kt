package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.soldierofheaven.weapons.Weapon

class WeaponSlot(private val weapon: Weapon, weaponIndex: Int, skin: Skin) : ImageButton(skin) {

    private val numberLabel = Label(weaponIndex.toString(), skin)
    private val weaponNameLabel = Label(weapon.name, skin)

    init {
        touchable = Touchable.disabled
        addActor(numberLabel)
        addActor(weaponNameLabel)
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        changeLabelPositions()
    }

    private fun changeLabelPositions() {
        //this condition is needed because of inheritance from ImageButton; setSize is called inside the constructor
        if (numberLabel == null || weaponNameLabel == null) return
        val paddingX = 5f
        numberLabel.setPosition(x + paddingX, y + height - numberLabel.height)
        weaponNameLabel.setPosition(x + paddingX, y)
    }
}
