package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.RigidBody
import java.util.*

@All(RigidBody::class)
class KnockbackSystem : IteratingSystem() {

    @Wire
    private var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    private val knockBackQueue = LinkedList<Int>()

    override fun begin() {

    }

    override fun process(entityId: Int) {

    }

    override fun end() {

    }
}
