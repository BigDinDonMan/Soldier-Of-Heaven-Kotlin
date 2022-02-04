package com.soldierofheaven.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.EventQueue
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.Weapon
import com.soldierofheaven.ecs.PlayerInputHandler
import com.soldierofheaven.ecs.components.Bullet
import com.soldierofheaven.ecs.components.Player
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.ecs.events.PlayerHealthChangeEvent
import com.soldierofheaven.ecs.events.ReloadFinishedEvent
import com.soldierofheaven.ecs.events.ReloadSuccessEvent
import com.soldierofheaven.ecs.events.ShotEvent
import com.soldierofheaven.ecs.systems.CameraPositioningSystem
import com.soldierofheaven.ecs.systems.RenderSystem
import com.soldierofheaven.ecs.systems.WeaponSystem
import com.soldierofheaven.ui.Crosshair
import com.soldierofheaven.ui.HealthBar
import com.soldierofheaven.ui.ReloadBar
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld
import com.soldierofheaven.util.heightF
import com.soldierofheaven.util.widthF
import net.mostlyoriginal.api.event.common.Event
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.properties.Delegates

//todo: add removal service so that we can remove entities from the game later

class GameScene(private val game: SoldierOfHeavenGame, private val ecsWorld: EcsWorld, private val physicsWorld: PhysicsWorld) : ScreenAdapter() {

    private val viewport = StretchViewport(Gdx.graphics.widthF(), Gdx.graphics.heightF())
    private val stage = Stage(viewport)

    private var debug = true
    private val debugRenderer = Box2DDebugRenderer()

    private val inputHandler = PlayerInputHandler()

    private val defaultSkin = Skin(Gdx.files.internal("skins/uiskin.json"))

    private val gameCamera = ecsWorld.getSystem(RenderSystem::class.java).gameCamera
    private lateinit var healthBar: HealthBar
    private lateinit var reloadBar: ReloadBar

    private var playerEntityId by Delegates.notNull<Int>()

    init {
        playerEntityId = ecsWorld.create()
        ecsWorld.getSystem(CameraPositioningSystem::class.java).playerEntityId = playerEntityId
        val playerWidth = 48f
        val playerHeight = 48f
        ecsWorld.edit(playerEntityId).add(Transform()).add(Player()).add(RigidBody().apply {
            val playerBodyDef = BodyDef().apply {
                gravityScale = 0f
                linearDamping = 5f
            }
            val playerBodyShape = PolygonShape().apply { setAsBox(playerWidth / 2, playerHeight / 2) }
            val playerBodyFixtureDef = FixtureDef().apply {
                shape = playerBodyShape
                friction = 2f
            }
            physicsBody = physicsWorld.createBody(playerBodyDef).apply { createFixture(playerBodyFixtureDef) }
            playerBodyShape.dispose()
        })
        initUi(ecsWorld.getEntity(playerEntityId).getComponent(Transform::class.java).position)
    }

    private fun initUi(playerPositionVector: Vector3) {
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

        reloadBar = ReloadBar(playerPositionVector, gameCamera, 50f).apply {
            width = 80f
            height = 10f
        }

        stage.addActor(reloadBar)
    }

    override fun show() {
        Gdx.input.inputProcessor = InputMultiplexer(inputHandler, stage)
    }

    override fun render(delta: Float) {
        ecsWorld.setDelta(delta)
        ecsWorld.process()

        if (debug) {
            debugRenderer.render(physicsWorld, ecsWorld.getSystem(RenderSystem::class.java).spriteBatch.projectionMatrix)
        }

        stage.act()
        stage.draw()

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.assetManager.get("sfx/pistol-shot.wav", Sound::class.java).play()
        }
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height)

    override fun dispose() {
        reloadBar.dispose()
        healthBar.dispose()
        defaultSkin.dispose()
        stage.dispose()
    }

    //<editor-fold desc="Event listeners">

    @Subscribe
    private fun updateHealthBar(e: PlayerHealthChangeEvent) {

    }

    @Subscribe
    private fun spawnBulletEntity(e: ShotEvent) {

    }

    @Subscribe
    private fun showReloadBar(e: ReloadSuccessEvent) {
        println("hej here")
        reloadBar.setWeapon(e.weapon)
        reloadBar.setEnabled(true)
    }

    @Subscribe
    private fun hideReloadBar(e: ReloadFinishedEvent) {
        reloadBar.setEnabled(false)
    }

    //</editor-fold>
}
