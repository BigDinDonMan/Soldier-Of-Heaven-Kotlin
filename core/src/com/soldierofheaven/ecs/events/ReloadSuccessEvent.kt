package com.soldierofheaven.ecs.events

import com.soldierofheaven.Weapon
import net.mostlyoriginal.api.event.common.Event

data class ReloadSuccessEvent(val weapon: Weapon) : Event
