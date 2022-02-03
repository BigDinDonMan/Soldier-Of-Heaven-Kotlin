package com.soldierofheaven

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.soldierofheaven.ecs.components.Bullet

// this class might turn out as a bit of a code smell but eh, whatever goes
class Weapon(
    val name: String,
    var clipSize: Int,
    var maxStoredAmmo: Int,
    var reloadTime: Float,
    var damage: Float,
    var fireRate: Float,
    val price: Int,
    var unlocked: Boolean,
    val weaponIcon: Texture,
    val bulletPrototype: Bullet,
    val shotSound: Sound,
    val reloadSound: Sound,
    var bulletSpread: Float = 0f,
    var bulletsPerShot: Int = 1
) {
    var shotCooldown = 0f
    var reloadCooldown = 0f
    var currentAmmo = clipSize
    var storedAmmo: Int

    init {
        storedAmmo = if (maxStoredAmmo == INFINITE_AMMO) INFINITE_AMMO else maxStoredAmmo / 2
    }

    companion object {
        const val INFINITE_AMMO = -1
    }

    fun update(delta: Float) {
        if (isReloading()) {
            reloadCooldown -= delta
        } else {
            if (shotCooldown > 0) {
                shotCooldown -= delta
            }
        }
    }

    fun isReloading(): Boolean = reloadCooldown > 0f

    fun tryFire(): Boolean {
        if (!canShoot() || isReloading()) return false
        if (isEmpty()) return tryReload()
        currentAmmo--
        shotCooldown = fireRate
        shotSound.play()
        return true
    }

    fun tryReload(): Boolean {
        if (isReloading() || currentAmmo == clipSize) {
            return false
        }
        if (maxStoredAmmo == INFINITE_AMMO) {
            currentAmmo = clipSize
            reloadCooldown = reloadTime
            reloadSound.play()
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
        reloadCooldown = reloadTime
        reloadSound.play()
        return true
    }

    fun isEmpty(): Boolean = currentAmmo <= 0

    fun canShoot(): Boolean = shotCooldown <= 0f
}
