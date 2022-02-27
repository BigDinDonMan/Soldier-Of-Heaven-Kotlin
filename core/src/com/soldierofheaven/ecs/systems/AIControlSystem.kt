package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.Enemy

@All(Enemy::class)
class AIControlSystem : IteratingSystem() {

    @Wire
    private var enemyMapper: ComponentMapper<Enemy>? = null

    override fun process(entityId: Int) {
        val enemy = enemyMapper!!.get(entityId)
        enemy.enemyStateMachine.update()
    }
}
