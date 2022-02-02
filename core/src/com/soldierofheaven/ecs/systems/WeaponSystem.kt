package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.soldierofheaven.Weapon
import com.soldierofheaven.ecs.events.MoveEvent
import com.soldierofheaven.ecs.events.WeaponChangeEvent
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe

class WeaponSystem(private val weapons: ArrayList<Weapon>) : BaseSystem() {

    @Wire
    var eventSystem: EventSystem? = null

    var shooting = false

    @Subscribe
    private fun receiveInput(e: WeaponChangeEvent) {
    }

    override fun processSystem() {
        val delta = Gdx.graphics.deltaTime

        if (shooting) {

        }
    }
}
