package com.soldierofheaven.weapons

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.google.gson.annotations.JsonAdapter
import com.soldierofheaven.EventQueue
import com.soldierofheaven.SoundManager
import com.soldierofheaven.ecs.events.ReloadFinishedEvent
import com.soldierofheaven.util.GameTimer

// this class might turn out as a bit of a code smell but eh, whatever goes
//todo: if implementing upgrades: add added[VarName] fields that will store added parameters (e.g. increased fire rate)
class Weapon(
    val name: String,
    var clipSize: Int,
    var maxStoredAmmo: Int,
    var reloadTime: Float,
    var damage: Float,
    var fireRate: Float,
    val price: Int,
    val weaponIcon: Texture,
    val ammoIcon: Texture,
    val bulletData: BulletData,
    val shotSound: Sound,
    val reloadSound: Sound,
    var bulletSpread: Float = 0f,
    var bulletsPerShot: Int = 1
) {
    var reloadTimer = GameTimer(reloadTime, false) { EventQueue.dispatch(ReloadFinishedEvent(this)) }
    var shotCooldown = 0f
    var currentAmmo = clipSize
    var storedAmmo: Int
    var unlocked: Boolean = false

    init {
        storedAmmo = if (maxStoredAmmo == INFINITE_AMMO) INFINITE_AMMO else maxStoredAmmo / 2
    }

    companion object {
        const val INFINITE_AMMO = -1
    }

    fun update(delta: Float) {
        reloadTimer.update(delta)
        if (shotCooldown > 0) {
            shotCooldown -= delta
        }
    }

    fun isReloading(): Boolean = reloadTimer.isRunning()

    fun tryFire(): Boolean {
        if (!canShoot() || isReloading()) return false
        if (isEmpty()) return false
        currentAmmo--
        shotCooldown = fireRate
        shotSound.play()
        SoundManager.queue(shotSound)
        return true
    }

    fun tryReload(): Boolean {
        if (isReloading() || currentAmmo == clipSize) {
            return false
        }
        if (maxStoredAmmo == INFINITE_AMMO) {
            currentAmmo = clipSize
            reloadTimer.start()
            reloadSound.play()
            SoundManager.queue(reloadSound)
            return true
        }
        val removed = clipSize - currentAmmo
        if (storedAmmo <= removed) {
            currentAmmo += storedAmmo
            storedAmmo = 0
        } else {
            currentAmmo = clipSize
            storedAmmo -= removed
        }
        reloadTimer.start()
        reloadSound.play()
        SoundManager.queue(reloadSound)
        return true
    }

    fun isEmpty(): Boolean = currentAmmo <= 0

    fun canShoot(): Boolean = shotCooldown <= 0f

    fun reloadProgress(): Float = reloadTimer.timeElapsed / reloadTime

    fun hasAmmo() = !isEmpty() && storedAmmo > 0

    fun reset() {
        reloadTimer.stop()
        reloadTimer.reset()
        storedAmmo = if (maxStoredAmmo == INFINITE_AMMO) -1 else maxStoredAmmo / 2
        shotCooldown = 0f
        currentAmmo = clipSize
        unlocked = false
    }
}
