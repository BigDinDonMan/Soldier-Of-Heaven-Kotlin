package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

data class MoveEvent(
    val moveDirectionX: Float,
    val moveDirectionY: Float
) : Event
