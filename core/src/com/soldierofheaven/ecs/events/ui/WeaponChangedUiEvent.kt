package com.soldierofheaven.ecs.events.ui

import com.soldierofheaven.weapons.Weapon
import net.mostlyoriginal.api.event.common.Event

data class WeaponChangedUiEvent(val weapon: Weapon, val index: Int) : Event
