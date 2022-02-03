package com.soldierofheaven

import net.mostlyoriginal.api.event.common.Event
import net.mostlyoriginal.api.event.common.EventSystem

object EventQueue {
    private val eventSystem = EventSystem()

    fun dispatch(e: Event) = eventSystem.dispatch(e)
}
