package com.soldierofheaven.ecs.events.ui

import com.soldierofheaven.weapons.Weapon
import net.mostlyoriginal.api.event.common.Event

data class WeaponUnlockedEvent(val weapon: Weapon) : Event
