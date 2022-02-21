package com.soldierofheaven

import com.artemis.WorldConfiguration
import net.mostlyoriginal.api.event.common.Event
import net.mostlyoriginal.api.event.common.EventSystem

object EventQueue {
    private val eventSystem = EventSystem()

    fun dispatch(e: Event) = eventSystem.dispatch(e)

    fun dispatchMultiple(vararg events: Event) = events.forEach(eventSystem::dispatch)

    fun init(config: WorldConfiguration) {
        config.setSystem(eventSystem)
    }

    fun register(o: Any) = eventSystem.registerEvents(o)
}
