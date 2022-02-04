package com.soldierofheaven.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.ecs.PlayerInputHandler
import com.soldierofheaven.ecs.components.*
import com.soldierofheaven.ecs.events.PlayerHealthChangeEvent
import com.soldierofheaven.ecs.events.ReloadFinishedEvent
import com.soldierofheaven.ecs.events.ReloadSuccessEvent
import com.soldierofheaven.ecs.events.ShotEvent
import com.soldierofheaven.ecs.systems.CameraPositioningSystem
import com.soldierofheaven.ecs.systems.RenderSystem
import com.soldierofheaven.ecs.systems.WeaponSystem
import com.soldierofheaven.ui.AmmoDisplay
import com.soldierofheaven.ui.Crosshair
import com.soldierofheaven.ui.HealthBar
import com.soldierofheaven.ui.ReloadBar
import com.soldierofheaven.util.*
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.math.abs
import kotlin.properties.Delegates
import kotlin.random.Random

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
    private lateinit var ammoDisplay: AmmoDisplay

    private var playerEntityId by Delegates.notNull<Int>()

    init {
        playerEntityId = ecsWorld.create()
        ecsWorld.getSystem(CameraPositioningSystem::class.java).playerEntityId = playerEntityId
        ecsWorld.getSystem(WeaponSystem::class.java).setPlayerEntityId(playerEntityId)
        val playerWidth = 48f
        val playerHeight = 48f
        ecsWorld.edit(playerEntityId).add(Player()).add(RigidBody().apply {
            val playerBodyDef = BodyDef().apply {
                gravityScale = 0f
                linearDamping = 5f
                type = BodyDef.BodyType.DynamicBody
            }
            val playerBodyShape = PolygonShape().apply { setAsBox(playerWidth / 2, playerHeight / 2) }
            val playerBodyFixtureDef = FixtureDef().apply {
                shape = playerBodyShape
                friction = 2f
            }
            physicsBody = physicsWorld.createBody(playerBodyDef).apply { createFixture(playerBodyFixtureDef) }
            playerBodyShape.dispose()
        }).create(Transform::class.java)
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

        val reloadIcon = game.assetManager.get("gfx/reload-bar.png", Texture::class.java)
        reloadBar = ReloadBar(reloadIcon, playerPositionVector, gameCamera, 50f)
        reloadBar.setBarSize(reloadBar.barWidth, 15f)
        reloadBar.barPaddingX = 5f

        stage.addActor(reloadBar)

        val ammoIcon = game.assetManager.get("gfx/bullet-basic.png", Texture::class.java)
        ammoDisplay = AmmoDisplay(ammoIcon, defaultSkin).apply {
            setPosition(healthPad, Gdx.graphics.heightF() - healthHeight - healthPad * 2 - ammoIcon.height)
        }
        ammoDisplay.update(ecsWorld.getSystem(WeaponSystem::class.java).weapons.first())
        stage.addActor(ammoDisplay)
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
        healthBar.updateDisplay(e.currentHealth)
    }

    @Subscribe
    private fun spawnBulletEntity(e: ShotEvent) {
        ammoDisplay.update(e.weapon)
        for (i in (0 until e.weapon.bulletsPerShot)) {
            val bulletId = ecsWorld.create()
            val editor = ecsWorld.edit(bulletId)
            editor.add(RigidBody().apply {
                val bulletBodyDef = BodyDef().apply {
                    gravityScale = 0f
                    linearDamping = 5f
                    type = BodyDef.BodyType.DynamicBody
                }
                val bulletBodyShape = PolygonShape().apply { setAsBox(e.weapon.bulletData.icon.width / 2f, e.weapon.bulletData.icon.height / 2f) }
                val bulletFixtureDef = FixtureDef().apply {
                    shape = bulletBodyShape
                    friction = 0f
                    isSensor = true
                }
                physicsBody = physicsWorld.createBody(bulletBodyDef).apply { createFixture(bulletFixtureDef) }
                physicsBody!!.setTransform(e.x, e.y, 0f)
                bulletBodyShape.dispose()
            }).add(TextureDisplay().apply {
                texture = e.weapon.bulletData.icon
            }).create(Bullet::class.java).apply {
                if (e.weapon.bulletSpread.isCloseTo(0f)) {
                    moveDirection.set(e.directionX, e.directionY)
                } else {
                    moveDirection.set(
                        (e.directionX + Random.nextDouble((-e.weapon.bulletSpread).toDouble(), e.weapon.bulletSpread.toDouble())).toFloat(),
                        (e.directionY + Random.nextDouble((-e.weapon.bulletSpread).toDouble(), e.weapon.bulletSpread.toDouble())).toFloat()
                    )
                }

                speed = e.weapon.bulletData.speed
            }
            editor.create(LifeCycle::class.java).apply { lifeTime = 2.5f }
            editor.create(Transform::class.java).apply {
                size.set(e.weapon.bulletData.icon.width.toFloat(), e.weapon.bulletData.icon.height.toFloat())
                position.set(e.x - size.x / 2, e.y - size.y / 2, 0f)
            }
        }
    }

    @Subscribe
    private fun showReloadBar(e: ReloadSuccessEvent) {
        reloadBar.setWeapon(e.weapon)
        reloadBar.setEnabled(true)
    }

    @Subscribe
    private fun hideReloadBar(e: ReloadFinishedEvent) {
        reloadBar.setEnabled(false)
        ammoDisplay.update(e.weapon)
    }

    //</editor-fold>
}
