package com.soldierofheaven.weapons

import com.badlogic.gdx.graphics.Texture
import com.soldierofheaven.ecs.components.enums.ExplosiveType

//todo: add particle system key here (for missiles)
data class BulletData(
    val speed: Float,
    val bulletDamping: Float,
    val explosiveType: ExplosiveType?,
    val explosiveRange: Float?,
    val explodeOnContact: Boolean?,
    val explosionTimer: Float?,
    val particleEffectName: String?,
    val icon: Texture
)
