package com.soldierofheaven.ecs.events

import com.soldierofheaven.weapons.Weapon
import net.mostlyoriginal.api.event.common.Event

data class ShotEvent(
    val weapon: Weapon,
    val x: Float,
    val y: Float,
    val directionX: Float,
    val directionY: Float,
    val rotation: Float
) : Event
