package com.soldierofheaven.scenes

import com.artemis.BaseSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.*
import com.soldierofheaven.ecs.PlayerInputHandler
import com.soldierofheaven.ecs.components.*
import com.soldierofheaven.ecs.components.enums.PickUpType
import com.soldierofheaven.ecs.events.*
import com.soldierofheaven.ecs.events.ui.CurrencyChangedEvent
import com.soldierofheaven.ecs.events.ui.StoredAmmoChangedEvent
import com.soldierofheaven.ecs.events.ui.WeaponChangedUiEvent
import com.soldierofheaven.ecs.events.ui.WeaponUnlockedEvent
import com.soldierofheaven.ecs.systems.*
import com.soldierofheaven.events.PauseEvent
import com.soldierofheaven.prototypes.Prefab
import com.soldierofheaven.prototypes.bullets.FireballPrefab
import com.soldierofheaven.prototypes.general.PlayerPrefab
import com.soldierofheaven.prototypes.pickups.PickUpPrefab
import com.soldierofheaven.stats.StatisticsTracker
import com.soldierofheaven.ui.*
import com.soldierofheaven.util.*
import com.soldierofheaven.util.`interface`.PlayerSystem
import com.soldierofheaven.util.`interface`.Resettable
import com.soldierofheaven.weapons.Weapon
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.properties.Delegates
import kotlin.random.Random

//NOTE: call reset on particle effect after loading or else it might behave weirdly for the first couple of times
class GameScene(private val game: SoldierOfHeavenGame, private val ecsWorld: EcsWorld, private val physicsWorld: PhysicsWorld) : ScreenAdapter(), Resettable {

    private val gameCamera: Camera = ecsWorld.getSystem(RenderSystem::class.java).gameCamera!!
    private val viewport = StretchViewport(Gdx.graphics.widthF(), Gdx.graphics.heightF())
    private val worldSpaceViewport = StretchViewport(Gdx.graphics.widthF(), Gdx.graphics.heightF(), gameCamera)
    private val stage = Stage(viewport)
    private val worldSpaceStage = Stage(worldSpaceViewport)

    private var debug = true
    private val debugRenderer = Box2DDebugRenderer()

    private val nonPausableSystems: Array<Class<out BaseSystem>> = arrayOf(
        AnimationSystem::class.java,
        RenderSystem::class.java
    )

    private var paused by Delegates.observable(false) { value, oldValue, newValue ->
        kotlin.run {
            inputHandler.setEnabled(!newValue)
            switchSystemsWorking(!newValue)
            pauseDialog.isVisible = newValue
            if (newValue) {
                pauseDialog.show(stage)
                SoundManager.pauseAll()
            } else {
                pauseDialog.hide()
                SoundManager.resumeAll()
            }
        }
    }

    private val inputHandler = PlayerInputHandler()

    private val defaultSkin = game.assetManager.get("skins/uiskin.json", Skin::class.java)
    private lateinit var pauseDialog: Dialog
    private lateinit var exitToMenuDialog: Dialog

    private lateinit var healthBar: HealthBar
    private lateinit var reloadBar: ReloadBar
    private lateinit var ammoDisplay: AmmoDisplay
    private lateinit var weaponSlots: List<WeaponSlot>
    private lateinit var scoreDisplay: ScoreDisplay
    private lateinit var currencyDisplay: CurrencyDisplay
    private lateinit var weaponUnlockWindow: WeaponUnlockWindow
    private lateinit var weaponNameLabel: WeaponNameLabel
    private lateinit var shoveAttackIcon: Image
    private lateinit var explosivesDisplay: ExplosivesDisplay

    private val calculationVector = Vector2()
    private val projectionVector = Vector3()

    private var playerEntityId by Delegates.notNull<Int>()

    private val fireballPrefab = FireballPrefab(ecsWorld, physicsWorld, game.assetManager)
    private val playerPrefab = PlayerPrefab(ecsWorld, physicsWorld, game.assetManager)

    /*MAPPERS*/
    private val healthMapper = ecsWorld.getMapper(Health::class.java)
    private val rigidBodyMapper = ecsWorld.getMapper(RigidBody::class.java)
    private val enemyMapper = ecsWorld.getMapper(Enemy::class.java)
    private val pickUpMapper = ecsWorld.getMapper(PickUp::class.java)
    private val transformMapper = ecsWorld.getMapper(Transform::class.java)


    private val fpsCounter = Label("60", defaultSkin).apply { isVisible = false }

    //todo: after adding pickup textures/animations then add them to prefab params here
    private val pickUpPrefabs = ObjectMap<PickUpType, Prefab>().apply {
        put(PickUpType.HEALTH, PickUpPrefab(ecsWorld, physicsWorld, game.assetManager).apply {
            prefabParams.put("payload", 25)
            prefabParams.put("pickUpType", PickUpType.HEALTH)
        })
        put(PickUpType.AMMO, PickUpPrefab(ecsWorld, physicsWorld, game.assetManager).apply {
            prefabParams.put("pickUpType", PickUpType.AMMO)
        })
        put(PickUpType.EXPLOSIVES, PickUpPrefab(ecsWorld, physicsWorld, game.assetManager).apply {
            prefabParams.put("pickUpType", PickUpType.EXPLOSIVES)
            prefabParams.put("payload", 1)
        })
    }

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

        val crosshair = Crosshair(game.assetManager["gfx/crosshair.png"])
        val reloadIcon = game.assetManager.get("gfx/reload-bar.png", Texture::class.java)
        reloadBar = ReloadBar(reloadIcon, playerPositionVector, gameCamera, 50f)
        reloadBar.setBarSize(reloadBar.barWidth, 15f)
        reloadBar.barPaddingX = 5f


        val slotsX = 15f
        val slotsStartY = 560f
        val slotSize = 72f
        val slotPadding = 15f
        val weaponSystem = ecsWorld.getSystem(WeaponSystem::class.java)
        val ammoIcon = weaponSystem.weapons.first().ammoIcon
        ammoDisplay = AmmoDisplay(ammoIcon, defaultSkin).apply {
            setPosition(healthPad, Gdx.graphics.heightF() - healthHeight - healthPad * 2 - ammoIcon.height)
        }
        ammoDisplay.update(ecsWorld.getSystem(WeaponSystem::class.java).weapons.first())
        var firstSlotMarkedAsSelected = false
        val slotsSkin = game.assetManager.get("skins/weapon-slot-skin.json", Skin::class.java)
        val lockedIcon = game.assetManager.get("gfx/padlock.png", Texture::class.java)
        weaponSlots = weaponSystem.weapons.mapIndexed { index, weapon -> kotlin.run {
            val slot = WeaponSlot(weapon, slotsSkin, lockedIcon, index + 1, defaultSkin)
            slot.setPosition(slotsX, slotsStartY - (slotSize + slotPadding) * index)
            slot.setSize(slotSize, slotSize)
            if (!firstSlotMarkedAsSelected) {
                firstSlotMarkedAsSelected = true
                slot.selected = true
            } else {
                slot.selected = false
            }
            slot
        } }
        weaponSlots.forEach(stage::addActor)

        pauseDialog = object : Dialog("Game paused", defaultSkin) {
            override fun result(`object`: Any?) {
                (`object` as? () -> Unit)?.invoke()
            }

            override fun show(stage: Stage?): Dialog {
                isVisible = true
                centerAbsolute()
                return this
            }

            override fun hide() {
                isVisible = false
            }
        }.apply {
            isVisible = false
            isModal = false
            isMovable = false
            titleLabel.isVisible = false
            setSize(300f, 150f)
            centerAbsolute()
        }.button("Resume", { paused = false; }).button("Exit", this::showExitToMenuDialog)

        exitToMenuDialog = object : Dialog("", defaultSkin){
            override fun result(`object`: Any) {
                (`object` as? () -> Unit)?.invoke()
            }

            override fun show(stage: Stage): Dialog {
                stage.addActor(this)
                pack()
                isVisible = true
                centerAbsolute()
                return this
            }

            override fun hide() {
                isVisible = false
                remove()
            }
        }.apply {
            isModal = false
            isVisible = false
            setSize(300f, 150f)
            contentTable.addActor(Label("Are you sure you want to quit?", defaultSkin))
            pack()
            centerAbsolute()
        }
        exitToMenuDialog.button("Yes", {
            exitToMenuDialog.hide()
            val oldScreen = game.shownScreen
            game.setScreen<MenuScene>()
            (oldScreen as? GameScene)?.reset()
        }).button("No", {
            hide()
            pauseDialog.show(stage)
            return@button
        })
        explosivesDisplay = ExplosivesDisplay(game.assetManager.get(Resources.BASIC_BULLET), game.assetManager.get("skins/uiskin.json"))
        explosivesDisplay.setPosition(healthPad + ammoDisplay.width + 10f + explosivesDisplay.width, ammoDisplay.y + ammoDisplay.height / 2 - explosivesDisplay.height / 2)
        shoveAttackIcon = Image(game.assetManager.get(Resources.BASIC_BULLET, Texture::class.java))
        shoveAttackIcon.setPosition(healthPad + ammoDisplay.width + 130f + shoveAttackIcon.width, ammoDisplay.y + ammoDisplay.height / 2 - shoveAttackIcon.height / 2)
        scoreDisplay = ScoreDisplay(defaultSkin)
        scoreDisplay.setPosition(Gdx.graphics.widthF() - scoreDisplay.width, Gdx.graphics.heightF() - scoreDisplay.height)
        currencyDisplay = CurrencyDisplay(game.assetManager.get("gfx/angelic-coin.png"), /*48f, 48f,*/ defaultSkin)
        currencyDisplay.setPosition(Gdx.graphics.widthF() - currencyDisplay.width, Gdx.graphics.heightF() - currencyDisplay.height - scoreDisplay.height)
        fpsCounter.setPosition(Gdx.graphics.widthF() - fpsCounter.width - 5f, 0f)
        stage.addActors(healthBar, reloadBar, crosshair, ammoDisplay, exitToMenuDialog,
            pauseDialog, currencyDisplay, scoreDisplay, fpsCounter, shoveAttackIcon, explosivesDisplay)

        weaponUnlockWindow = WeaponUnlockWindow(weaponSystem.weapons, defaultSkin).apply { isVisible = false }
        weaponUnlockWindow.centerAbsolute()
        stage.addActor(weaponUnlockWindow)

        weaponNameLabel = WeaponNameLabel(rigidBodyMapper.get(playerEntityId).physicsBody!!.position, defaultSkin)
        worldSpaceStage.addActor(weaponNameLabel)
    }

    override fun show() {
        Gdx.input.inputProcessor = InputMultiplexer(inputHandler, stage)
    }

    override fun render(delta: Float) {
        ecsWorld.update(delta)

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            fpsCounter.isVisible = !fpsCounter.isVisible
        }

        updateFPS()

        if (debug) {
            debugRenderer.render(
                physicsWorld,
                ecsWorld.getSystem(RenderSystem::class.java).spriteBatch!!.projectionMatrix
            )

//            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
//                EventQueue.dispatch(ExplosionEvent(150f , 150f, 0f, 100f, 500f))
//            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
                StatisticsTracker.currency += 2000
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
                weaponUnlockWindow.isVisible = !weaponUnlockWindow.isVisible
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                EventQueue.dispatch(CameraShakeEvent(5f, 2f))
            }
        }

        stage.update()
        worldSpaceStage.update()
    }

    private fun updateFPS() {
        if (fpsCounter.isVisible) {
            fpsCounter.setText(Gdx.graphics.framesPerSecond)
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        worldSpaceViewport.update(width, height)
    }

    override fun dispose() {
        reloadBar.dispose()
        healthBar.dispose()
        stage.dispose()
        worldSpaceStage.dispose()
    }

    //<editor-fold desc="Event listeners">

    @Subscribe
    private fun updateHealthBar(e: PlayerHealthChangeEvent) {
        healthBar.updateDisplay(e.currentHealth)
    }

    @Subscribe
    private fun updateSelectedWeapon(e: WeaponChangedUiEvent) {
        ammoDisplay.update(e.weapon)

        val index = e.index - 1

        if (weaponSlots.indexOfFirst { it.selected } != index) {
            val swapSound: Sound = game.assetManager["sfx/weapon-swap.wav"];
            swapSound.play()
        }

        weaponSlots.forEach { it.selected = false }
        weaponSlots[index].selected = true

        weaponNameLabel.update(e.weapon)
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
                physicsBody = Physics.newCircleBody(bulletId, e.weapon.bulletData.icon.width / 2f,
                    gravityScale = 1f, bullet = true, friction = 0f, isSensor = true)
                physicsBody!!.setTransform(e.x, e.y, 0f)
            }).add(TextureDisplay().apply {
                texture = e.weapon.bulletData.icon
            }).create(Bullet::class.java).apply {
                if (e.weapon.bulletSpread.isCloseTo(0f)) {
                    moveDirection.set(e.directionX, e.directionY)
                } else {
                    moveDirection.set(
                        (e.directionX + Random.nextFloat(-e.weapon.bulletSpread, e.weapon.bulletSpread)),
                        (e.directionY + Random.nextFloat(-e.weapon.bulletSpread, e.weapon.bulletSpread))
                    )
                }
                explosionTimer = e.weapon.bulletData.explosionTimer
                explosiveType = e.weapon.bulletData.explosiveType
                explosionRange = e.weapon.bulletData.explosiveRange
                explodeOnContact = e.weapon.bulletData.explodeOnContact
                bulletDamping = e.weapon.bulletData.bulletDamping
                explosionStrength = e.weapon.bulletData.explosionStrength
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
            editor.create(Tag::class.java).apply { value = Tags.BULLET }
        }
    }

    @Subscribe
    private fun spawnExplosive(e: ExplosiveThrowEvent) {
        if (StatisticsTracker.explosives <= 0) return

        projectionVector.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        gameCamera.unproject(projectionVector)

        val playerRigidBody = rigidBodyMapper.get(playerEntityId)

        calculationVector.set(projectionVector.x, projectionVector.y).
            sub(playerRigidBody.physicsBody!!.position.x, playerRigidBody.physicsBody!!.position.y).
            nor()

        val explosiveId = ecsWorld.create()
        val editor = ecsWorld.edit(explosiveId)
        val tex = editor.create(TextureDisplay::class.java).apply { texture = game.assetManager.get(Resources.BASIC_BULLET) }
        editor.create(Transform::class.java).apply { size.set(tex.texture!!.width.toFloat(), tex.texture!!.height.toFloat()) }
        editor.create(Damage::class.java).apply { value = 150f }
        editor.create(Speed::class.java).apply { value = 400f }
        editor.create(Explosive::class.java).apply {
            range = 150f
            strength = 100f
            damping = 3.5f
            fuseTime = 3f
            moveDirection.set(calculationVector)
        }
        editor.create(Tag::class.java).apply { value = Tags.EXPLOSIVE }
        editor.create(RigidBody::class.java).apply {
            physicsBody = Physics.newCircleBody(explosiveId, tex.texture!!.width.toFloat() / 2, 0f, isSensor = true)

            physicsBody!!.setTransform(playerRigidBody.physicsBody!!.position.x, playerRigidBody.physicsBody!!.position.y, physicsBody!!.angle)
        }

        StatisticsTracker.explosives--
        explosivesDisplay.update()
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
        val part = edit.create(ParticleEffect::class.java).apply {
            particleEffect = ParticlePools.obtain("Explosion").apply {
                reset()
                start()
            }
            particleEffectName = "Explosion"
        }
        edit.create(LifeCycle::class.java).apply {
            lifeTime = part.particleEffect!!.emitters.maxBy { it.duration }!!.duration / 1000f // duration is in millis, lifetime should be in seconds
        }

//        val explosionSound: Sound = game.assetManager["sfx/explosion.wav"]
//        explosionSound.play()
    }

    @Subscribe
    private fun togglePause(_e: PauseEvent) {
        if (exitToMenuDialog.isVisible) return
        paused = !paused
    }

    @Subscribe
    private fun handleEnemyKilled(e: EnemyKilledEvent) {
        //this is a sum because StatisticsTracker is updated after this listener runs
        scoreDisplay.update(StatisticsTracker.score + e.score)
        currencyDisplay.update(StatisticsTracker.currency + e.currency)

        removeEnemyHealthBar(e.enemyId)

        val enemyComp = enemyMapper.get(e.enemyId) ?: return
        val transform = transformMapper.get(e.enemyId) ?: return
        val shouldDropPickUp = Random.nextFloat() < enemyComp.pickUpDropChance
        if (shouldDropPickUp) {
            val chance = (Random.nextFloat() * 100).toInt()
            val matches = enemyComp.pickUpDropMap.filter { chance in it.value.first..it.value.second }
            if (matches.isEmpty()) return

            val type = matches.first().key
            //we need to use the transform here because at this point, enemy rigidbody is freed
            val spawnX = transform.position.x + transform.size.x / 2
            val spawnY = transform.position.y + transform.size.y / 2
            var weapon: Weapon? = null
            if (type == PickUpType.AMMO) {
                weapon = getRandomWeapon() ?: return
            }
            val pickUpId = pickUpPrefabs.get(type).instantiate(spawnX, spawnY)
            if (type == PickUpType.AMMO) {
                val pickUp = pickUpMapper.get(pickUpId)
                val ammoGained = weapon!!.maxStoredAmmo / 10
                pickUp.pickUpPayload = PickUp.AmmoInfo(ammoGained, weapon)
            }
        }
    }

    private fun removeEnemyHealthBar(enemyId: Int) {
        val toRemove = worldSpaceStage.actors.filterIsInstance<EnemyHealthBar>().find { it.enemyId == enemyId }
        toRemove?.remove()
        toRemove?.dispose()
    }

    @Subscribe
    private fun handleDamageEvent(e: DamageEvent) {
        if (e.entityId == playerEntityId) {
            val playerHealth = healthMapper.get(e.entityId)
            healthBar.updateDisplay((playerHealth.health - e.damage).toInt())
            if (playerHealth.health - e.damage <= 0f) {
                EventQueue.dispatch(PlayerDeathEvent())
            }
        }

        val rigidBody = rigidBodyMapper.get(e.entityId)
        if (rigidBody?.physicsBody != null) {
            val damageTakenLabel = buildPopupLabel(e.damage.toString(), rigidBody.physicsBody!!.position.x, rigidBody.physicsBody!!.position.y, 50f)
            worldSpaceStage.addActor(damageTakenLabel)
        }
    }

    @Subscribe
    private fun handlePickUp(e: PickUpEvent) {
        when (e.pickUp.pickUpType) {
            PickUpType.HEALTH -> {
                val playerHealth = healthMapper.get(playerEntityId)
                val oldHealth = playerHealth.health
                playerHealth.health += e.pickUp.pickUpPayload as Int
                healthBar.updateDisplay(playerHealth.health.toInt())
                EventQueue.dispatch(PlayerHealthChangeEvent(oldHealth.toInt(), playerHealth.health.toInt()))
            }
            PickUpType.AMMO -> {
                //find weapon and add ammo value
                //if selected weapon is the same as picked up ammo then also update ui
                val (ammoAmount, weapon) = e.pickUp.pickUpPayload as PickUp.AmmoInfo
                val targetWeapon = ecsWorld.getSystem(WeaponSystem::class.java).weapons.first { it.name == weapon.name }
                targetWeapon.storedAmmo += ammoAmount
                EventQueue.dispatch(StoredAmmoChangedEvent(targetWeapon))
            }
            PickUpType.EXPLOSIVES -> {
                StatisticsTracker.explosives += e.pickUp.pickUpPayload as Int
            }
        }
    }

    @Subscribe
    private fun handlePlayerDeath(_e: PlayerDeathEvent) {
        //todo: stop all pausable systems, show game over dialog with stats and stop all sounds
        //todo: call reset after the whole simulation (maybe add a post-simulation callback?)
    }

    @Subscribe
    private fun handleCurrencyChanged(e: CurrencyChangedEvent) {
        currencyDisplay.update(e.newCurrency)
    }

    @Subscribe
    private fun handleAmmoChanged(e: StoredAmmoChangedEvent) {
        if (e.weapon.name == ecsWorld.getSystem(WeaponSystem::class.java).currentWeapon.name) {
            ammoDisplay.update(e.weapon)
        }
    }

    @Subscribe
    private fun handleWeaponUnlock(e: WeaponUnlockedEvent) {
        weaponSlots.first { it.weapon.name == e.weapon.name }.update()
    }

    @Subscribe
    private fun handleShove(_e: ShoveEvent) {
        shoveAttackIcon.color.a = 0.3f
        shoveAttackIcon.addAction(alpha(1f, 0.75f))
    }
    //</editor-fold>

    private fun getRandomWeapon(): Weapon? {
        //skip first weapon and get all of them that are unlocked
        val weapons = ecsWorld.getSystem(WeaponSystem::class.java).weapons.drop(1).filter { it.unlocked }
        return if (weapons.isEmpty()) null else weapons[Random.nextInt(0, weapons.size)]
    }

    override fun reset() {
        gameCamera.position.set(Vector3.Zero)
        val weaponSystem = ecsWorld.getSystem(WeaponSystem::class.java)
        weaponSystem.weapons.forEach { it.reset() }
        weaponSystem.weapons.first().unlocked = true
        weaponSystem.resetCurrentWeapon()
        ecsWorld.removeAllEntities { id ->
            val body = rigidBodyMapper.get(id)
            if (body?.physicsBody != null) {
                physicsWorld.destroyBody(body.physicsBody)
                body.physicsBody = null
            }
        }
        ammoDisplay.update(weaponSystem.weapons.first())
        weaponSlots.forEach {
            it.selected = false
        }
        weaponSlots[0].selected = true
        paused = false
        setupScene()
        reloadBar.playerPositionVector = ecsWorld.getEntity(playerEntityId).getComponent(Transform::class.java).position
        reloadBar.setEnabled(false)
        weaponNameLabel.playerPositionRef = ecsWorld.getEntity(playerEntityId).getComponent(RigidBody::class.java).physicsBody!!.position
        healthBar.updateDisplay(150)
        weaponUnlockWindow.reset()
        scoreDisplay.reset()
        currencyDisplay.reset()
        StatisticsTracker.reset()
        SoundManager.stopAll()
        SoundManager.clearQueue()
    }

    private fun setupScene(setupUi: Boolean = false) {
        playerEntityId = playerPrefab.instantiate()

        ecsWorld.systems.filterIsInstance<PlayerSystem>().forEach { it.setPlayerEntityId(playerEntityId) }

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
        edit.create(Tag::class.java).apply { value = Tags.ENEMY }
        edit.add(RigidBody().apply {
            physicsBody = Physics.newBoxBody(testEnemyId, transform.size.x / 2, transform.size.y / 2,
                gravityScale = 0f, linearDamping = 5f, friction = 2f)
        }).create(Health::class.java).apply { maxHealth = 50f }

//        val aiEnemyId = ecsWorld.create()
//        val aiEdit = ecsWorld.edit(aiEnemyId)
//        val aiTransform = aiEdit.create(Transform::class.java).apply {
//            position.set(-100f, -100f, 0f)
//            size.set(40f, 40f)
//        }
//        aiEdit.create(RigidBody::class.java).apply {
//            physicsBody = Physics.newBoxBody(aiEnemyId, aiTransform.size.x / 2, aiTransform.size.y / 2,
//                gravityScale = 0f, linearDamping = 5f, friction = 2f)
//        }
//        aiEdit.create(Tag::class.java).apply { value = Tags.ENEMY }
//        aiEdit.create(Enemy::class.java).apply {
//            ownerId = aiEnemyId
//            playerPositionRef = ecsWorld.getEntity(playerEntityId).getComponent(RigidBody::class.java).physicsBody!!.position
////            shotStopRange = 240f
////            runAwayDistance = 150f
////            shotInterval = 0.5f
////            bulletPrefab = fireballPrefab
//            currencyOnKill = 25
//            scoreOnKill = 100
//            pickUpDropChance = 1f
//            pickUpDropMap.put(PickUpType.HEALTH, Pair(0, 10))
//            pickUpDropMap.put(PickUpType.AMMO, Pair(10, 75))
//            pickUpDropMap.put(PickUpType.EXPLOSIVES, Pair(90, 100))
//        }
//        aiEdit.create(Speed::class.java).apply { value = 25f }
//        aiEdit.create(Health::class.java).apply { maxHealth = 80f }
//        aiEdit.create(Damage::class.java).apply { value = 10f }
//        aiEdit.create(ContactDamage::class.java).apply {
//            value = 10f
//            knockback = 15f
//        }
//
//        worldSpaceStage.addActor(
//            EnemyHealthBar(aiEnemyId, rigidBodyMapper.get(aiEnemyId).physicsBody!!.position, healthMapper.get(aiEnemyId), barRounding = 5f)
//        )
    }

    private fun switchSystemsWorking(working: Boolean) {
        ecsWorld.systems.filter { it.javaClass !in nonPausableSystems }.forEach { it.isEnabled = working }
    }

    private fun showExitToMenuDialog() {
        exitToMenuDialog.show(stage)
    }

    private fun buildPopupLabel(s: String, x: Float, y: Float, maxYOffset: Float): Label {
        val label = Label(s, defaultSkin)
        label.setPosition(x, y, Align.center)
        label.addAction(sequence(
            moveToAligned(x, y + maxYOffset, Align.center, 0.3f, Interpolation.smooth),
            fadeOut(1f),
            removeActor()
        ))
        return label
    }
}
