package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.soldierofheaven.EventQueue
import com.soldierofheaven.Weapon
import com.soldierofheaven.ecs.events.ShotEvent
import com.soldierofheaven.ecs.events.ShotRequestEvent
import com.soldierofheaven.ecs.events.WeaponChangeEvent
import com.soldierofheaven.ecs.events.ui.WeaponChangedUiEvent
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe

class WeaponSystem(private val weapons: ArrayList<Weapon>) : BaseSystem() {

    @Wire
    var eventSystem: EventSystem? = null

    private var shooting = false
    private var currentWeapon: Weapon = weapons.first()

    @Subscribe
    private fun receiveInput(e: WeaponChangeEvent) {
        if (e.weaponIndex > weapons.size) return
        if (currentWeapon.isReloading()) return

        val targetWeapon = weapons[e.weaponIndex - 1]
        if (!targetWeapon.unlocked) return

        currentWeapon = targetWeapon
        EventQueue.dispatch(WeaponChangedUiEvent(currentWeapon, e.weaponIndex))
    }

    @Subscribe
    private fun receiveShotState(e: ShotRequestEvent) {
        shooting = e.start
    }

    override fun processSystem() {
        val delta = Gdx.graphics.deltaTime

        currentWeapon.update(delta)
        if (shooting) {
            if (currentWeapon.tryFire()) {
                EventQueue.dispatch(ShotEvent(currentWeapon))
            }
        }
    }
}
