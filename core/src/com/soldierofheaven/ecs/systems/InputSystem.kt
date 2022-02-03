package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.soldierofheaven.ecs.components.Player
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.ecs.events.MoveEvent
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
    private fun updateMoveDirection(e: MoveEvent) {
        moveDirection.set(e.moveDirectionX, e.moveDirectionY)
    }

    override fun process(entityId: Int) {
        val rigidBody = rigidBodyMapper!!.get(entityId)
        val player = playerMapper!!.get(entityId)
        rigidBody.physicsBody!!.applyLinearImpulse(
            moveDirection.x * player.speed,
            moveDirection.y * player.speed,
            rigidBody.physicsBody!!.position.x,
            rigidBody.physicsBody!!.position.y,
            true)
    }
}
