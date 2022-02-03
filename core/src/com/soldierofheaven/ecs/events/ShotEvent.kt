package com.soldierofheaven.ecs.events

import com.soldierofheaven.Weapon
import net.mostlyoriginal.api.event.common.Event

data class ShotEvent(val weapon: Weapon) : Event
