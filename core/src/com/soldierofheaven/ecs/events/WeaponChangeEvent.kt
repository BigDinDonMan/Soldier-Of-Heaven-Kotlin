package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

data class WeaponChangeEvent(val weaponIndex: Int) : Event
