package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

data class KnockbackEvent(val entityId: Int, val strength: Float, val directionX: Float, val directionY: Float): Event
