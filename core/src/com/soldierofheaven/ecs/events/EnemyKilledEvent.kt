package com.soldierofheaven.ecs.events

import net.mostlyoriginal.api.event.common.Event

class EnemyKilledEvent(val enemyId: Int, val score: Int, val currency: Int) : Event
