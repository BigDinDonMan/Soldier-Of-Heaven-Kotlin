package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import com.soldierofheaven.EventQueue
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.weapons.Weapon
import com.soldierofheaven.ecs.events.*
import com.soldierofheaven.ecs.events.ui.WeaponChangedUiEvent
import com.soldierofheaven.util.math.directionTowards
import com.soldierofheaven.util.math.rotationTowards
import net.mostlyoriginal.api.event.common.Subscribe

class WeaponSystem(val weapons: List<Weapon> = ArrayList()) : BaseSystem() {
    private var shooting = false
    private var currentWeapon: Weapon = weapons.first()

    @Wire(name = "gameCamera")
    private var camera: Camera? = null

    @Wire
    private var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    private val projectionVector = Vector3()

    private var playerEntityId = 0

    override fun processSystem() {
        val delta = Gdx.graphics.deltaTime
        currentWeapon.update(delta)

        if (shooting) {
            if (currentWeapon.tryFire()) {
                val playerRigidBody = rigidBodyMapper!!.get(playerEntityId)
                projectionVector.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), projectionVector.z)
                camera!!.unproject(projectionVector)
                val playerBodyPosition = playerRigidBody.physicsBody!!.position
                val moveDirectionVector = playerBodyPosition.directionTowards(projectionVector.x, projectionVector.y, projectionVector)
                val rotation = rotationTowards(moveDirectionVector.x, moveDirectionVector.y)
                moveDirectionVector.nor()
                EventQueue.dispatch(ShotEvent(
                    currentWeapon,
                    playerBodyPosition.x + moveDirectionVector.x,
                    playerBodyPosition.y + moveDirectionVector.y,
                    moveDirectionVector.x,
                    moveDirectionVector.y,
                    rotation)
                )
            } else if (currentWeapon.isEmpty() && currentWeapon.canShoot()) {
                currentWeapon.tryReload()
                EventQueue.dispatch(ReloadSuccessEvent(currentWeapon))
            }
        }
    }

    fun setPlayerEntityId(id: Int) {
        this.playerEntityId = id
    }

    fun resetCurrentWeapon() {
        currentWeapon = weapons.first()
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
