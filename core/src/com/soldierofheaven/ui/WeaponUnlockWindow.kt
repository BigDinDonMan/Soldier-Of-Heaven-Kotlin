package com.soldierofheaven.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.soldierofheaven.EventQueue
import com.soldierofheaven.ecs.events.ui.CurrencyChangedEvent
import com.soldierofheaven.ecs.events.ui.StoredAmmoChangedEvent
import com.soldierofheaven.stats.StatisticsTracker
import com.soldierofheaven.util.`interface`.Resettable
import com.soldierofheaven.util.centerAbsolute
import com.soldierofheaven.weapons.Weapon

//todo: add weapon buy callback
class WeaponUnlockWindow(private val weapons: List<Weapon>, skin: Skin) : Window("", skin), Resettable {

    private val buyButtons: MutableList<ImageTextButton> = ArrayList()
    private lateinit var nextWaveButton: ImageTextButton

    companion object {
        const val MAX_ROW_ITEMS = 3
    }

    init {
        initUi()
    }

    private fun initUi() {
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
                    //todo: play unlock/ammo buy sound (ammo buy is maybe the same as weapon swap? and unlock is sth like a choir)
                    if (weapon.unlocked) {
                        buyAmmoForWeapon(weapon)
                    } else {
                        unlockWeapon(weapon)
                        buyButton.text = "Buy 10% of max ammo (${weapon.ammoPrice}$)"
                    }
                    updateButtons()
                    pack()
                    centerAbsolute()
                }
            })
            //todo: make all these widget groups the same size in the end
            widgetsGroup.addActor(weaponImage)
            widgetsGroup.addActor(nameLabel)
            widgetsGroup.addActor(buyButton)
            add(widgetsGroup)

            buyButtons += buyButton
        } }
        row()
        nextWaveButton = ImageTextButton("Next wave!", skin)

        val buttonPadding = 15f
        add(nextWaveButton).center().bottom().expand(true, false).colspan(3).padTop(buttonPadding).padBottom(buttonPadding)
        pack()
    }

    private fun buyAmmoForWeapon(w: Weapon) {
        if (StatisticsTracker.currency < w.ammoPrice) return

        val oldCurrency = StatisticsTracker.currency
        w.storedAmmo += w.maxStoredAmmo / 10
        StatisticsTracker.currency -= w.ammoPrice
        EventQueue.dispatchMultiple(
            StoredAmmoChangedEvent(w),
            CurrencyChangedEvent(oldCurrency, StatisticsTracker.currency)
        )
    }

    private fun unlockWeapon(w: Weapon) {
        if (StatisticsTracker.currency < w.price) return
        if (w.unlocked) return

        w.unlocked = true
        val oldCurrency = StatisticsTracker.currency
        StatisticsTracker.currency -= w.price
        EventQueue.dispatchMultiple(CurrencyChangedEvent(oldCurrency, StatisticsTracker.currency))
    }

    private fun updateButtons() {
        //we skip the first weapon because thats the pistol with infinite ammo
        val start = 1 //1
        for (i in start until weapons.size) {
            val button = buyButtons[i - start]
            val weapon = weapons[i]
            val disabled = StatisticsTracker.currency < (if (weapon.unlocked) weapon.ammoPrice else weapon.price) || weapon.storedAmmo == weapon.maxStoredAmmo
            button.isDisabled = disabled
            button.touchable = if (disabled) Touchable.disabled else Touchable.enabled
        }
    }

    override fun reset() {
        super.reset()
        initUi()
    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)

        updateButtons()
    }

    fun setWaveCallback(callback: () -> Unit) {
        nextWaveButton.clearListeners()
        nextWaveButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                callback.invoke()
            }
        })
    }
}
