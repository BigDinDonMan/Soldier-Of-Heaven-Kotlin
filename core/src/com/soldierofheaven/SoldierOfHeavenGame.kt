package com.soldierofheaven

import com.artemis.WorldConfiguration
import com.artemis.WorldConfigurationBuilder
import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.utils.ScreenUtils
import com.soldierofheaven.ecs.systems.PhysicsSystem
import com.soldierofheaven.ecs.systems.RenderSystem
import com.soldierofheaven.scenes.GameScene
import com.soldierofheaven.scenes.MenuScene
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld
import ktx.app.KtxGame
import net.mostlyoriginal.api.event.common.EventSystem

class SoldierOfHeavenGame : KtxGame<Screen>() {
    private lateinit var batch: SpriteBatch
    private lateinit var physicsWorld: PhysicsWorld
    private lateinit var ecsWorld: EcsWorld
    private lateinit var eventBus: EventSystem
    private lateinit var assetManager: AssetManager

    override fun create() {
        Box2D.init()
        batch = SpriteBatch()
        // we do not need any gravity in this game
        physicsWorld = PhysicsWorld(Vector2.Zero, false)

        val ecsWorldConfig = WorldConfigurationBuilder().with(
            PhysicsSystem(physicsWorld),
            RenderSystem(batch)
        ).build()
        eventBus = EventSystem()
        ecsWorldConfig.setSystem(eventBus)
        ecsWorld = EcsWorld(ecsWorldConfig)

        addScreen(MenuScene(this))
        addScreen(GameScene(this, ecsWorld, physicsWorld))
    }

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
    }

    override fun dispose() {
        batch.dispose()
        physicsWorld.dispose()
    }
}
