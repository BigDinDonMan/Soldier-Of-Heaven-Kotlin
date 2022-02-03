package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

data class ShotRequestEvent(
    val start: Boolean
) : Event
