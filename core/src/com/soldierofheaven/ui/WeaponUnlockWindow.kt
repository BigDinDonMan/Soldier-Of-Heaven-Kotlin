package com.soldierofheaven.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.soldierofheaven.stats.StatisticsTracker
import com.soldierofheaven.util.`interface`.Resettable
import com.soldierofheaven.weapons.Weapon
import net.mostlyoriginal.api.event.common.Subscribe

class WeaponUnlockWindow(private val weapons: List<Weapon>, skin: Skin) : Window("", skin), Resettable {

    private val buyButtons: MutableList<ImageTextButton> = ArrayList()

    init {
        initUi()
    }

    private fun initUi() {
        val slotsTable = Table()
        val lockedWeapons = weapons.filter { !it.unlocked }
        lockedWeapons.forEach { weapon -> kotlin.run {
            //weapon entry widget should contain:
            //weapon icon with button and weapon name label underneath (button changes appearance & functionality after unlocking the weapon)
            val buyButton = ImageTextButton("Unlock (${weapon.price}$)", skin)
            val nameLabel = Label(weapon.name, skin)
            val weaponImage = Image(weapon.weaponIcon)
            val widgetsGroup = VerticalGroup().apply { space(10f) }

            buyButton.addListener(object : ClickListener(){
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    if (weapon.unlocked) {
                        buyAmmoForWeapon(weapon)
                    } else {
                        unlockWeapon(weapon)
                        buyButton.text = "Buy 10% of max ammo (${weapon.ammoPrice}$)"
                    }
                    buyButton.isDisabled = StatisticsTracker.currency < weapon.ammoPrice
                }
            })
            //todo: make all these widget groups the same size in the end
            widgetsGroup.addActor(weaponImage)
            widgetsGroup.addActor(nameLabel)
            widgetsGroup.addActor(buyButton)
            add(widgetsGroup)

            buyButtons += buyButton
        } }
        pack()
    }

    private fun buyAmmoForWeapon(w: Weapon) {

    }

    private fun unlockWeapon(w: Weapon) {

    }

    override fun reset() {
        super.reset()
        initUi()
    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)

        val lockedWeapons = weapons.filter { !it.unlocked }
        for (i in lockedWeapons.indices) {
            if (lockedWeapons[i].unlocked) {
                buyButtons[i].isDisabled = StatisticsTracker.currency < lockedWeapons[i].ammoPrice
            } else {
                buyButtons[i].isDisabled = StatisticsTracker.currency < lockedWeapons[i].price
            }
        }
    }
}
