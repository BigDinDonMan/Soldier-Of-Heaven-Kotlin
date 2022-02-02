package com.soldierofheaven

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.soldierofheaven.ecs.components.Bullet

// this class might turn out as a bit of a code smell but eh, whatever goes
class Weapon(
    val name: String,
    var clipSize: Int,
    var storedAmmo: Int,
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
    private var shotCooldown = 0f
    private var reloadCooldown = 0f

    fun update(delta: Float) {

    }

    fun isReloading(): Boolean {
        return false
    }

    fun tryFire(): Boolean {
        return false
    }
}
