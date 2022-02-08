package com.soldierofheaven.ecs.events

import com.soldierofheaven.ecs.components.Explosive
import net.mostlyoriginal.api.event.common.Event

data class ExplosiveCollisionEvent(val entityId: Int) : Event
