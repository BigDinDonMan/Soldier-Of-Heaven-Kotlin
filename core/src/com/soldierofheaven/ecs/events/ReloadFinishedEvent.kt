package com.soldierofheaven.ecs.events

import com.soldierofheaven.Weapon
import net.mostlyoriginal.api.event.common.Event

class ReloadFinishedEvent(val weapon: Weapon) : Event
