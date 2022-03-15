package com.soldierofheaven.ecs.events.debug

import net.mostlyoriginal.api.event.common.Event

class DebugLineEvent(val startX: Float, val startY: Float, val endX: Float, val endY: Float) : Event {
}
