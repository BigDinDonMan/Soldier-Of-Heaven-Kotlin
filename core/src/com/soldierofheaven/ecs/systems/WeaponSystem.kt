package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.soldierofheaven.Weapon
import com.soldierofheaven.ecs.events.MoveEvent
import com.soldierofheaven.ecs.events.ReloadEvent
import com.soldierofheaven.ecs.events.ShootEvent
import com.soldierofheaven.ecs.events.WeaponChangeEvent
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe

class WeaponSystem(private val weapons: ArrayList<Weapon>) : BaseSystem() {

    @Wire
    var eventSystem: EventSystem? = null

    private var shooting = false
    private var currentWeapon: Weapon = weapons.first()

    @Subscribe
    private fun receiveInput(e: WeaponChangeEvent) {

    }

    @Subscribe
    private fun receiveShotState(e: ShootEvent) {
        shooting = e.start
    }

    override fun processSystem() {
        val delta = Gdx.graphics.deltaTime

        currentWeapon.update(delta)
//        if (shooting) {
//            if (currentWeapon.tryFire()) {
//                //dispatch event for bullet instantiation
//            }
//        }
//
//        if (currentWeapon.isReloading()) {
//            currentWeapon.reloadCooldown -= delta
//        } else {
//            if (currentWeapon.shotCooldown > 0) currentWeapon.shotCooldown -= delta
//        }
    }
}
