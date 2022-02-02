package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.soldierofheaven.ecs.components.Player
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.ecs.events.InputEvent
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe

@All(Player::class)
class InputSystem : IteratingSystem() {
    @Wire
    var transformMapper: ComponentMapper<Transform>? = null
    @Wire
    var rigidBodyMapper: ComponentMapper<RigidBody>? = null
    @Wire
    var playerMapper: ComponentMapper<Player>? = null
    @Wire
    var eventSystem: EventSystem? = null

    private val moveDirection = Vector2()

    @Subscribe
    private fun updateMoveDirection(e: InputEvent) {
        moveDirection.set(e.moveDirectionX, e.moveDirectionY)
    }

    override fun process(entityId: Int) {
        val transform = transformMapper!!.get(entityId)
        val rigidBody = rigidBodyMapper!!.get(entityId)
    }
}
