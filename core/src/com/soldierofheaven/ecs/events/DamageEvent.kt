package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

data class DamageEvent(val entityId: Int, val damage: Float) : Event
