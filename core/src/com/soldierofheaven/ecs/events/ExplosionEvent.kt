package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

//todo: add range & damage
data class ExplosionEvent(val centerX: Float, val centerY: Float, val damage: Float, val range: Float) : Event
