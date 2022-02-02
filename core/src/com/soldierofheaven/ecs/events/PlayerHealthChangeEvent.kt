package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

class PlayerHealthChangeEvent(
    val oldHealth: Int,
    val currentHealth: Int
) : Event
