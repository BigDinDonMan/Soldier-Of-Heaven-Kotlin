package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.soldierofheaven.weapons.Weapon

//slot skin is temporary; everything will be inside skin parameter later on down the line (but it works for now)
class WeaponSlot(val weapon: Weapon, private val slotSkin: Skin, private val lockedIcon: Texture, weaponIndex: Int, skin: Skin) : Table(skin) {

    class WeaponSlotStyle(var background: Drawable?) {
        constructor() : this(null) {}
    }

    private val numberLabel = Label(weaponIndex.toString(), skin)
    private val weaponIconImage = Image()
    var selected = false
        set(value) {
            field = value
            update()
        }

    init {
        touchable = Touchable.disabled
        weaponIconImage.drawable = TextureRegionDrawable(weapon.weaponIcon)
        background = slotSkin.getDrawable("weapon-slot-idle")
        addActor(numberLabel)
        add(weaponIconImage).center()
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        val paddingY = 2f
        val paddingX = 7.5f
        numberLabel.setPosition(paddingX, height - numberLabel.height - paddingY)
    }

    fun update() {
        if (selected) {
            background = slotSkin.getDrawable("weapon-slot-selected")
        } else if (weapon.unlocked) {
            background = slotSkin.getDrawable("weapon-slot-idle")
            weaponIconImage.drawable = TextureRegionDrawable(weapon.weaponIcon)
        } else {
            background = slotSkin.getDrawable("weapon-slot-disabled")
            weaponIconImage.drawable = TextureRegionDrawable(lockedIcon)
        }
    }
}
