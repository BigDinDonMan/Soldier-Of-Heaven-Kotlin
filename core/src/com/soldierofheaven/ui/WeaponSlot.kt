package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.soldierofheaven.weapons.Weapon

class WeaponSlot(private val weapon: Weapon, weaponIndex: Int, skin: Skin) : ImageButton(skin) {

    private val numberLabel = Label(weaponIndex.toString(), skin)
    private val weaponNameLabel = Label(weapon.name, skin)
    private val weaponIconImage = Image()

    init {
        touchable = Touchable.disabled
        weaponIconImage.drawable = TextureRegionDrawable(weapon.weaponIcon)
        addActor(numberLabel)
        addActor(weaponNameLabel)
        addActor(weaponIconImage)
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        changeChildrenPositions()
    }

    private fun changeChildrenPositions() {
        //this condition is needed because of inheritance from ImageButton; setSize is called inside the constructor
        if (numberLabel == null || weaponNameLabel == null || weaponIconImage == null) return
        val paddingX = 5f
        numberLabel.setPosition(x + paddingX, y + height - numberLabel.height)
        weaponNameLabel.setPosition(x + paddingX, y)
        weaponIconImage.setPosition(
            x + width / 2 - weapon.weaponIcon.width / 2,
            y + height / 2 - weapon.weaponIcon.height / 2
        )
        weaponIconImage.pack()
    }

    fun setSelected(b: Boolean) {

    }
}
