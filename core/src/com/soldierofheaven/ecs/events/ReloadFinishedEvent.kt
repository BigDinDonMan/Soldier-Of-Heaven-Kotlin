package com.soldierofheaven.ecs.events

import com.soldierofheaven.weapons.Weapon
import net.mostlyoriginal.api.event.common.Event

class ReloadFinishedEvent(val weapon: Weapon) : Event
