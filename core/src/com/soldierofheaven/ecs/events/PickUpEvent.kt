package com.soldierofheaven.ecs.events

import com.soldierofheaven.ecs.components.PickUp
import net.mostlyoriginal.api.event.common.Event

data class PickUpEvent(val pickUp: PickUp): Event
