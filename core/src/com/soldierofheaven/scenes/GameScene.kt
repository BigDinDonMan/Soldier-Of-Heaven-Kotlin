package com.soldierofheaven.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.ecs.PlayerInputHandler
import com.soldierofheaven.ecs.components.Player
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.ecs.systems.RenderSystem
import com.soldierofheaven.ui.Crosshair
import com.soldierofheaven.ui.HealthBar
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld
import com.soldierofheaven.util.heightF
import com.soldierofheaven.util.widthF
import net.mostlyoriginal.api.event.common.Event
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.properties.Delegates

class GameScene(private val game: SoldierOfHeavenGame, private val ecsWorld: EcsWorld, private val physicsWorld: PhysicsWorld) : ScreenAdapter() {

    private val viewport = StretchViewport(Gdx.graphics.widthF(), Gdx.graphics.heightF())
    private val stage = Stage(viewport)

    private var debug = true
    private val debugRenderer = Box2DDebugRenderer()

    private val inputHandler = PlayerInputHandler(ecsWorld.getSystem(EventSystem::class.java))

    private val defaultSkin = Skin(Gdx.files.internal("skins/uiskin.json"))
    private lateinit var healthBar: HealthBar

    private var playerEntityId by Delegates.notNull<Int>()

    init {
        initUi()
        playerEntityId = ecsWorld.create()
        ecsWorld.edit(playerEntityId).add(Transform()).add(Player())
    }

    private fun initUi() {
        val healthPad = 10f
        val healthHeight = 50f
        val healthWidth = 250f
        healthBar = HealthBar(150, defaultSkin).apply {
            width = healthWidth
            height = healthHeight
            setPosition(healthPad, Gdx.graphics.heightF() - healthHeight - healthPad)
        }
        stage.addActor(healthBar)

        val crosshair = Crosshair(game.assetManager.get("gfx/crosshair.png"))
        stage.addActor(crosshair)
    }

    override fun show() {
        Gdx.input.inputProcessor = InputMultiplexer(stage, inputHandler)
    }

    @Subscribe
    private fun updateHealthBar(e: Event) {

    }

    override fun render(delta: Float) {
        ecsWorld.setDelta(delta)
        ecsWorld.process()

        stage.act()
        stage.draw()
        if (debug) {
            debugRenderer.render(physicsWorld, ecsWorld.getSystem(RenderSystem::class.java).spriteBatch.projectionMatrix)
        }
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height)

    override fun dispose() {
        stage.dispose()
        healthBar.dispose()
    }
}
