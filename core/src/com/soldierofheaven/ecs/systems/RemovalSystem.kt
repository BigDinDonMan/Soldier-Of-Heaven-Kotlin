package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.EntitySystem
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.soldierofheaven.ecs.components.LifeCycle
import java.util.*

@All(LifeCycle::class)
class RemovalSystem : IteratingSystem() {

    @Wire
    var lifeCycleMapper: ComponentMapper<LifeCycle>? = null

    private val removalQueue = LinkedList<Int>()

    override fun process(entityId: Int) {
        val lifeCycle = lifeCycleMapper!!.get(entityId)
        lifeCycle.lifeTime -= world.delta
        if (lifeCycle.lifeTime <= 0f) {
            removalQueue += entityId
        }
    }

    override fun end() {
        removalQueue.forEach(world::delete)
        removalQueue.clear()
    }
}
