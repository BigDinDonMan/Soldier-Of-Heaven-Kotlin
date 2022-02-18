package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

data class CameraShakeEvent(val strength: Float, val duration: Float) : Event
