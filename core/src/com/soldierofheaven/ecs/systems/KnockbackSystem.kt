package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.events.KnockbackEvent
import com.soldierofheaven.util.applyImpulseToCenter
import net.mostlyoriginal.api.event.common.Subscribe
import java.util.*

@All(RigidBody::class)
class KnockbackSystem : BaseSystem() {

    @Wire
    private var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    private val knockBackQueue = LinkedList<KnockbackEvent>()

    override fun processSystem() {
        knockBackQueue.forEach { (id, strength, dirX, dirY) -> kotlin.run {
            val rigidBody = rigidBodyMapper!!.get(id)
            if (rigidBody?.physicsBody == null) return

            rigidBody.physicsBody!!.applyImpulseToCenter(
                dirX * strength,
                dirY * strength,
                true
            )
        } }
    }

    override fun end() {
        knockBackQueue.clear()
    }

    @Subscribe
    private fun queueKnockbackEvent(e: KnockbackEvent) {
        knockBackQueue += e
    }
}
