package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.badlogic.gdx.Gdx
import com.soldierofheaven.EventQueue
import com.soldierofheaven.weapons.Weapon
import com.soldierofheaven.ecs.events.*
import com.soldierofheaven.ecs.events.ui.WeaponChangedUiEvent
import net.mostlyoriginal.api.event.common.Subscribe

class WeaponSystem(val weapons: List<Weapon> = ArrayList()) : BaseSystem() {
    private var shooting = false
    private var currentWeapon: Weapon = weapons.first()

    override fun processSystem() {
        val delta = Gdx.graphics.deltaTime
        currentWeapon.update(delta)

        if (shooting) {
            if (currentWeapon.tryFire()) {
                //todo: get button start position and normalized move direction
//                EventQueue.dispatch(ShotEvent(currentWeapon))
            } else if (currentWeapon.isEmpty() && currentWeapon.canShoot()) {
                currentWeapon.tryReload()
                EventQueue.dispatch(ReloadSuccessEvent(currentWeapon))
            }
        }
    }

    //<editor-fold desc="Event listeners">
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

    @Subscribe
    private fun receiveInput(e: WeaponChangeEvent) {
        if (e.weaponIndex > weapons.size) return
        if (currentWeapon.isReloading()) return

        val targetWeapon = weapons[e.weaponIndex - 1]
        if (!targetWeapon.unlocked) return

        currentWeapon = targetWeapon
        EventQueue.dispatch(WeaponChangedUiEvent(currentWeapon, e.weaponIndex))
    }
    //</editor-fold>
}
