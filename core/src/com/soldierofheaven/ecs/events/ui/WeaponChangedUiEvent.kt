package com.soldierofheaven.ecs.events.ui

import com.soldierofheaven.Weapon
import net.mostlyoriginal.api.event.common.Event

data class WeaponChangedUiEvent(val weapon: Weapon, val index: Int) : Event
