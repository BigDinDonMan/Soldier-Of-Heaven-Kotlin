package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

data class ShootEvent(
    val start: Boolean
) : Event
