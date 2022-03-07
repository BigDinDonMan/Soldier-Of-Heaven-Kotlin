package com.soldierofheaven.ecs.events.ui

import com.soldierofheaven.weapons.Weapon
import net.mostlyoriginal.api.event.common.Event

class StoredAmmoChangedEvent(val weapon: Weapon) : Event
