package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

data class ExplosionEvent(val centerX: Float, val centerY: Float) : Event
