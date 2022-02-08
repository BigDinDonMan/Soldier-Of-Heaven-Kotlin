package com.soldierofheaven.util.serialization

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.soldierofheaven.ecs.components.enums.ExplosiveType
import com.soldierofheaven.weapons.Weapon

typealias BulletPrefabData = com.soldierofheaven.weapons.BulletData

//this class is BAD but I didn't find any good way to inject asset manager into a JsonAdapter annotation so...
class WeaponJsonConverter(private val assetManager: AssetManager) {

    private data class WeaponData(
        val name: String,
        val clipSize: Int,
        var maxStoredAmmo: Int,
        var reloadTime: Float,
        var damage: Float,
        var fireRate: Float,
        val price: Int,
        val weaponIconPath: String,
        val ammoIconPath: String,
        val bulletData: BulletData,
        val shotSoundPath: String,
        val reloadSoundPath: String,
        var unlocked: Boolean = false,
        var bulletSpread: Float = 0f,
        var bulletsPerShot: Int = 1
    )

    private data class BulletData(
        val speed: Float,
        val bulletDamping: Float,
        val explosiveType: ExplosiveType?,
        val explosiveRange: Float?,
        val explodeOnContact: Boolean?,
        val explosionTimer: Float?,
        val particleEffectName: String?,
        val iconPath: String
    )

    fun toJson(obj: Collection<Weapon>): String {
        val gson = Gson()
        val data = obj.map { convert(it) }
        return gson.toJson(data)
    }

    fun fromJson(str: String): List<Weapon> {
        val gson = Gson()
        val data = gson.fromJson<List<WeaponData>>(str, object : TypeToken<List<WeaponData>>(){}.type)
        return data.map { convertBack(it) }
    }

    private fun convert(w: Weapon) = WeaponData(
        w.name, w.clipSize, w.maxStoredAmmo, w.reloadTime, w.damage, w.fireRate,
        w.price, assetManager.getAssetFileName(w.weaponIcon), assetManager.getAssetFileName(w.ammoIcon),
        BulletData(w.bulletData.speed, w.bulletData.bulletDamping, w.bulletData.explosiveType, w.bulletData.explosiveRange,
            w.bulletData.explodeOnContact, w.bulletData.explosionTimer, w.bulletData.particleEffectName, assetManager.getAssetFileName(w.bulletData.icon)),
        assetManager.getAssetFileName(w.shotSound), assetManager.getAssetFileName(w.reloadSound),
        unlocked = w.unlocked, bulletSpread = w.bulletSpread, bulletsPerShot = w.bulletsPerShot
    )

    private fun convertBack(w: WeaponData) = Weapon(
        w.name, w.clipSize, w.maxStoredAmmo, w.reloadTime, w.damage,
        w.fireRate, w.price, assetManager.get(w.weaponIconPath), assetManager.get(w.ammoIconPath),
        BulletPrefabData(w.bulletData.speed, w.bulletData.bulletDamping, w.bulletData.explosiveType, w.bulletData.explosiveRange,
            w.bulletData.explodeOnContact, w.bulletData.explosionTimer, w.bulletData.particleEffectName, assetManager.get(w.bulletData.iconPath)),
        assetManager.get(w.shotSoundPath), assetManager.get(w.reloadSoundPath), w.bulletSpread,
        w.bulletsPerShot
    ).apply { unlocked = w.unlocked }
}
