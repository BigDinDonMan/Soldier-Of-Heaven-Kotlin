package com.soldierofheaven.ecs.events.ui

import net.mostlyoriginal.api.event.common.Event

data class CurrencyChangedEvent(val oldCurrency: Int, val newCurrency: Int) : Event
