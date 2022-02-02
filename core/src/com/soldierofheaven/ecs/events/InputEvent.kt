package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

data class InputEvent(
    val moveDirectionX: Float,
    val moveDirectionY: Float,
    val leftMouseDown: Boolean
) : Event
