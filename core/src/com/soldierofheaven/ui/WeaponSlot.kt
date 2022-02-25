package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.soldierofheaven.weapons.Weapon

//slot skin is temporary; everything will be inside skin parameter later on down the line (but it works for now)
class WeaponSlot(private val weapon: Weapon, private val slotSkin: Skin, weaponIndex: Int, skin: Skin) : Table(skin) {

    private val numberLabel = Label(weaponIndex.toString(), skin)
    private val weaponIconImage = Image()
    var selected = false
        set(value) {
            field = value
            background = slotSkin.getDrawable(if (value) "weapon-slot-selected" else "weapon-slot-idle")
        }

    init {
        touchable = Touchable.disabled
        weaponIconImage.drawable = TextureRegionDrawable(weapon.weaponIcon)
        background = slotSkin.getDrawable("weapon-slot-idle")
        addActor(numberLabel)
        addActor(weaponIconImage)
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        changeChildrenPositions()
    }

    private fun changeChildrenPositions() {
        //this condition is needed because of inheritance from ImageButton; setSize is called inside the constructor
        if (numberLabel == null || weaponIconImage == null) return
        val paddingX = 5f
        numberLabel.setPosition(x + paddingX, y + height - numberLabel.height)
        weaponIconImage.setPosition(
            x + width / 2 - weapon.weaponIcon.width / 2,
            y + height / 2 - weapon.weaponIcon.height / 2
        )
        weaponIconImage.pack()
    }
}
