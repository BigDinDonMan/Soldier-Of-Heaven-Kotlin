package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.soldierofheaven.EventQueue
import com.soldierofheaven.Weapon
import com.soldierofheaven.ecs.events.*
import com.soldierofheaven.ecs.events.ui.WeaponChangedUiEvent
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe

class WeaponSystem(val weapons: List<Weapon> = ArrayList()) : BaseSystem() {

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

    @Subscribe
    private fun receiveReloadRequest(e: ReloadRequestEvent) {
        val success = currentWeapon.tryReload()
        if (success) {
            EventQueue.dispatch(ReloadSuccessEvent(currentWeapon))
        }
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
