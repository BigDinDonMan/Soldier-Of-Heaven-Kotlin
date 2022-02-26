package com.soldierofheaven.ecs.systems

import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.Enemy

@All(Enemy::class)
class AIControlSystem : IteratingSystem() {

    override fun process(entityId: Int) {
        println(entityId)
    }
}
