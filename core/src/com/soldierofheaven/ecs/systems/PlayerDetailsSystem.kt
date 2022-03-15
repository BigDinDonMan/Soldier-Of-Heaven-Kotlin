package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.RayCastCallback
import com.soldierofheaven.EventQueue
import com.soldierofheaven.Tags
import com.soldierofheaven.ecs.components.Damage
import com.soldierofheaven.ecs.components.Player
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.events.DamageEvent
import com.soldierofheaven.ecs.events.KnockbackEvent
import com.soldierofheaven.ecs.events.ShoveEvent
import com.soldierofheaven.ecs.events.debug.DebugLineEvent
import com.soldierofheaven.util.PhysicsWorld
import com.soldierofheaven.util.`interface`.PlayerSystem
import com.soldierofheaven.util.physics.GenericRayCastCallback
import net.mostlyoriginal.api.event.common.Subscribe

class PlayerDetailsSystem : BaseSystem(), PlayerSystem {

    private var playerEntityId = 0

    @Wire
    private var playerMapper: ComponentMapper<Player>? = null

    @Wire
    private var rigidBodyMapper: ComponentMapper<RigidBody>? = null

    @Wire(name = "physicsWorld")
    private var physicsWorld: PhysicsWorld? = null

    @Wire(name = "gameCamera")
    private var camera: Camera? = null

    private val calculationVector = Vector2()
    private val projectionVector = Vector3()

    override fun processSystem() {
        val delta = world.delta
        val player = playerMapper!!.get(playerEntityId)

        if (player.currentKickTimer >= 0f) {
            player.currentKickTimer -= delta
        }

        if (player.currentHitTimer >= 0f) {
            player.currentHitTimer -= delta
        }
    }

    @Subscribe
    private fun handleShoveEvent(e: ShoveEvent) {
        val player = playerMapper!!.get(playerEntityId)
        if (player.currentKickTimer > 0f) return
        val rigidBody = rigidBodyMapper!!.get(playerEntityId)
        val playerPos = rigidBody.physicsBody!!.position

        //calculation vector now holds the end point of raycasting
        projectionVector.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera!!.unproject(projectionVector)

        calculationVector.
            set(projectionVector.x, projectionVector.y).
            sub(playerPos).
            nor()

        val dirX = calculationVector.x
        val dirY = calculationVector.y

        calculationVector.set(playerPos.x, playerPos.y).add(dirX * player.kickRange, dirY * player.kickRange)
        val callback = GenericRayCastCallback(world, Tags.ENEMY)
        physicsWorld!!.rayCast(callback, playerPos, calculationVector)

        for (entityId in callback.hitResults) {
            EventQueue.dispatchMultiple(
                DamageEvent(entityId, player.kickDamage),
                KnockbackEvent(entityId, player.kickStrength, dirX, dirY)
            )
        }
    }

    override fun setPlayerEntityId(id: Int) {
        playerEntityId = id
    }
}
