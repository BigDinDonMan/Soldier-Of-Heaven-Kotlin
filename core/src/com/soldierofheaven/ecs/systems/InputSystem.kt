package com.soldierofheaven.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.soldierofheaven.ecs.components.Player
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Speed
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.ecs.events.MoveEvent
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
    var speedMapper: ComponentMapper<Speed>? = null

    private val moveDirection = Vector2()

    @Subscribe
    private fun updateMoveDirection(e: MoveEvent) {
        moveDirection.set(e.moveDirectionX, e.moveDirectionY)
    }

    override fun process(entityId: Int) {
        val rigidBody = rigidBodyMapper!!.get(entityId)
        val speed = speedMapper!!.get(entityId)
        rigidBody.physicsBody!!.applyLinearImpulse(
            moveDirection.x * speed.value,
            moveDirection.y * speed.value,
            rigidBody.physicsBody!!.position.x,
            rigidBody.physicsBody!!.position.y,
            true)
    }
}
