package com.soldierofheaven.scenes

import com.artemis.managers.WorldSerializationManager
import com.artemis.prefab.Prefab
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.*
import com.soldierofheaven.ecs.PlayerInputHandler
import com.soldierofheaven.ecs.components.*
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.ecs.components.enums.ExplosiveType
import com.soldierofheaven.ecs.events.*
import com.soldierofheaven.ecs.events.ui.WeaponChangedUiEvent
import com.soldierofheaven.ecs.systems.CameraPositioningSystem
import com.soldierofheaven.ecs.systems.PhysicsSystem
import com.soldierofheaven.ecs.systems.RenderSystem
import com.soldierofheaven.ecs.systems.WeaponSystem
import com.soldierofheaven.stats.StatisticsTracker
import com.soldierofheaven.ui.*
import com.soldierofheaven.util.*
import com.soldierofheaven.util.`interface`.Resettable
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.math.abs
import kotlin.properties.Delegates
import kotlin.random.Random

//NOTE: call reset on particle effect after loading or else it might behave weirdly for the first couple of times
class GameScene(private val game: SoldierOfHeavenGame, private val ecsWorld: EcsWorld, private val physicsWorld: PhysicsWorld) : ScreenAdapter(), Resettable {

    private val viewport = StretchViewport(Gdx.graphics.widthF(), Gdx.graphics.heightF())
    private val stage = Stage(viewport)

    private var debug = true
    private val debugRenderer = Box2DDebugRenderer()

    private val inputHandler = PlayerInputHandler()

    private val defaultSkin = Skin(Gdx.files.internal("skins/uiskin.json"))

    private val gameCamera: Camera = ecsWorld.getSystem(RenderSystem::class.java).gameCamera!!
    private lateinit var healthBar: HealthBar
    private lateinit var reloadBar: ReloadBar
    private lateinit var ammoDisplay: AmmoDisplay
    private lateinit var weaponSlots: List<WeaponSlot>

    private var tracker = StatisticsTracker()

    private var playerEntityId by Delegates.notNull<Int>()

    init {
        setupScene(setupUi = true)
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

        val crosshair = Crosshair(game.assetManager["gfx/crosshair.png"])
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

        val slotsX = 15f
        val slotsStartY = 560f
        val slotSize = 72f
        val slotPadding = 15f
        val weaponSystem = ecsWorld.getSystem(WeaponSystem::class.java)
        var firstSlotMarkedAsSelected = false
        weaponSlots = weaponSystem.weapons.mapIndexed { index, weapon -> kotlin.run {
            val slot = WeaponSlot(weapon, index + 1, defaultSkin)
            slot.setSize(slotSize, slotSize)
            slot.setPosition(slotsX, slotsStartY - (slotSize + slotPadding) * index)
            if (!firstSlotMarkedAsSelected) {
                firstSlotMarkedAsSelected = true
                slot.setSelected(true)
            }
            slot
        } }
        weaponSlots.forEach(stage::addActor)
    }

    override fun show() {
        Gdx.input.inputProcessor = InputMultiplexer(inputHandler, stage)
    }

    override fun render(delta: Float) {
        ecsWorld.update(delta)

        if (debug) {
            debugRenderer.render(
                physicsWorld,
                ecsWorld.getSystem(RenderSystem::class.java).spriteBatch!!.projectionMatrix
            )

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                EventQueue.dispatch(ExplosionEvent(150f , 150f))
            }
        }

        stage.update()
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
    private fun updateSelectedWeapon(e: WeaponChangedUiEvent) {
        ammoDisplay.update(e.weapon)

        val swapSound: Sound = game.assetManager["sfx/weapon-swap.wav"];
        swapSound.play()

        weaponSlots.forEach { it.setSelected(false) }
        weaponSlots[e.index - 1].setSelected(true)
    }

    @Subscribe
    private fun updateAmmoAfterShooting(e: ShotEvent) {
        ammoDisplay.update(e.weapon)
    }

    @Subscribe
    private fun spawnBulletEntity(e: ShotEvent) {
        for (i in (0 until e.weapon.bulletsPerShot)) {
            val bulletId = ecsWorld.create()
            val editor = ecsWorld.edit(bulletId)
            val bullet = editor.add(RigidBody().apply {
                val bulletBodyDef = BodyDef().apply {
                    gravityScale = 0f
                    type = BodyDef.BodyType.DynamicBody
                    bullet = true
                }
                val bulletBodyShape = PolygonShape().apply { setAsBox(e.weapon.bulletData.icon.width / 2f, e.weapon.bulletData.icon.height / 2f) }
                val bulletFixtureDef = FixtureDef().apply {
                    shape = bulletBodyShape
                    friction = 0f
                    isSensor = true
                }
                physicsBody = physicsWorld.createBody(bulletBodyDef).apply {
                    createFixture(bulletFixtureDef)
                    userData = bulletId
                }
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
                explosionTimer = e.weapon.bulletData.explosionTimer
                explosiveType = e.weapon.bulletData.explosiveType
                explosionRange = e.weapon.bulletData.explosiveRange
                explodeOnContact = e.weapon.bulletData.explodeOnContact
                bulletDamping = e.weapon.bulletData.bulletDamping
            }
            editor.create(LifeCycle::class.java).apply { lifeTime = 2.5f }
            editor.create(Transform::class.java).apply {
                size.set(e.weapon.bulletData.icon.width.toFloat(), e.weapon.bulletData.icon.height.toFloat())
                position.set(e.x - size.x / 2, e.y - size.y / 2, 0f)
            }
            editor.create(Damage::class.java).apply {
                damageableTags.add(Tags.ENEMY)
                value = e.weapon.damage
            }
            editor.create(Speed::class.java).apply {
                value = e.weapon.bulletData.speed
            }
            editor.add(Tag().apply { value = Tags.BULLET })
        }
    }

    @Subscribe
    private fun spawnExplosive(e: ExplosiveThrowEvent) {
        val explosiveId = ecsWorld.create()
        val editor = ecsWorld.edit(explosiveId)
        editor.create(TextureDisplay::class.java).apply {  }
        editor.create(Transform::class.java).apply {  }
        editor.create(Damage::class.java).apply {  }
        editor.create(Speed::class.java).apply {  }
        editor.create(Explosive::class.java).apply {  }
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

    @Subscribe
    private fun spawnExplosion(e: ExplosionEvent) {
        val explosionEffectId = ecsWorld.create()
        val edit = ecsWorld.edit(explosionEffectId)
        edit.create(Transform::class.java).apply { position.set(e.centerX, e.centerY, 0f) }
        val part = edit.create(com.soldierofheaven.ecs.components.ParticleEffect::class.java).apply {
            particleEffect = ParticlePools.obtain("Explosion").apply {
                reset()
                start()
            }
            particleEffectName = "Explosion"
        }
        edit.create(LifeCycle::class.java).apply {
            lifeTime = part.particleEffect!!.emitters.maxBy { it.duration }!!.duration / 1000f // duration is in millis, lifetime should be in seconds
        }
    }

    //</editor-fold>

    override fun reset() {
        gameCamera.position.set(Vector3.Zero)
        val weaponSystem = ecsWorld.getSystem(WeaponSystem::class.java)
        weaponSystem.weapons.forEach { it.reset() }
        weaponSystem.resetCurrentWeapon()
        tracker = StatisticsTracker()
        val ids = ecsWorld.getSystem(PhysicsSystem::class.java).entityIds
        val rigidbodyMapper = ecsWorld.getMapper(RigidBody::class.java)
        for (i in 0 until ids.size()) {
            val body = rigidbodyMapper.get(ids.get(i))
            if (body?.physicsBody != null) {
                physicsWorld.destroyBody(body.physicsBody)
            }
        }
        ammoDisplay.update(weaponSystem.weapons.first())
        ecsWorld.entityManager.reset()
        setupScene()
    }

    private fun setupScene(setupUi: Boolean = false) {
        playerEntityId = ecsWorld.create()
        ecsWorld.getSystem(CameraPositioningSystem::class.java).playerEntityId = playerEntityId
        ecsWorld.getSystem(WeaponSystem::class.java).setPlayerEntityId(playerEntityId)
        val playerWidth = 48f
        val playerHeight = 48f
        val editor = ecsWorld.edit(playerEntityId)
        editor.add(Player()).add(Tag().apply { value = Tags.PLAYER }).add(RigidBody().apply {
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
            physicsBody = physicsWorld.createBody(playerBodyDef).apply {
                createFixture(playerBodyFixtureDef)
                userData = playerEntityId
            }
            playerBodyShape.dispose()
        }).create(Transform::class.java)
        editor.create(Speed::class.java).apply { value = 25f }
        editor.create(Health::class.java)
        if (setupUi) {
            initUi(ecsWorld.getEntity(playerEntityId).getComponent(Transform::class.java).position)
        }

        val testId = ecsWorld.create()
        val testeditor = ecsWorld.edit(testId)
        val tex = testeditor.create(TextureDisplay::class.java).apply { texture = game.assetManager.get(Resources.BASIC_BULLET) }
        testeditor.create(Transform::class.java).apply {
            position.set(50f, 50f, 0f)
            size.set(tex.texture!!.width.toFloat(), tex.texture!!.height.toFloat())
        }

        val testEnemyId = ecsWorld.create()
        val edit = ecsWorld.edit(testEnemyId)
        val texture = edit.create(TextureDisplay::class.java).apply { texture = game.assetManager.get(Resources.BASIC_BULLET) }
        val transform = edit.create(Transform::class.java).apply {
            position.set(100f, 100f, 0f)
            size.set(texture.texture!!.width.toFloat(), texture.texture!!.height.toFloat())
        }
        edit.add(Tag().apply { value = Tags.ENEMY }).add(RigidBody().apply {
            val playerBodyDef = BodyDef().apply {
                gravityScale = 0f
                linearDamping = 5f
                type = BodyDef.BodyType.DynamicBody
            }
            val playerBodyShape = PolygonShape().apply { setAsBox(transform.size.x / 2, transform.size.y / 2) }
            val playerBodyFixtureDef = FixtureDef().apply {
                shape = playerBodyShape
                friction = 2f
            }
            physicsBody = physicsWorld.createBody(playerBodyDef).apply {
                createFixture(playerBodyFixtureDef)
                userData = testEnemyId
            }
            playerBodyShape.dispose()
        }).create(Health::class.java).apply { maxHealth = 50f }
    }
}
