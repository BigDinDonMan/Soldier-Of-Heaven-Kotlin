package com.soldierofheaven.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.ecs.PlayerInputHandler
import com.soldierofheaven.ecs.systems.RenderSystem
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld
import com.soldierofheaven.util.heightF
import com.soldierofheaven.util.widthF
import net.mostlyoriginal.api.event.common.Event
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe

class GameScene(private val game: SoldierOfHeavenGame, private val ecsWorld: EcsWorld, private val physicsWorld: PhysicsWorld) : ScreenAdapter() {

    private val viewport = StretchViewport(Gdx.graphics.widthF(), Gdx.graphics.heightF())
    private val stage = Stage(viewport)

    private var debug = true
    private val debugRenderer = Box2DDebugRenderer()

    private val inputHandler = PlayerInputHandler(ecsWorld.getSystem(EventSystem::class.java))

    override fun show() {
        Gdx.input.inputProcessor = InputMultiplexer(stage, inputHandler)
    }

    @Subscribe
    private fun updateHealthBar(e: Event) {

    }

    override fun render(delta: Float) {
        ecsWorld.setDelta(delta)
        ecsWorld.process()

        if (debug) {
            debugRenderer.render(physicsWorld, ecsWorld.getSystem(RenderSystem::class.java).spriteBatch.projectionMatrix)
        }
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height)

    override fun dispose() {
        stage.dispose()
    }
}
