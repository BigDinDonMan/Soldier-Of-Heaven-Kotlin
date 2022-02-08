package com.soldierofheaven

import com.artemis.WorldConfigurationBuilder
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.soldierofheaven.ecs.components.Bullet
import com.soldierofheaven.ecs.components.ParticleEffect
import com.soldierofheaven.ecs.components.enums.ExplosiveType
import com.soldierofheaven.ecs.systems.*
import com.soldierofheaven.scenes.GameScene
import com.soldierofheaven.scenes.MenuScene
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld
import com.soldierofheaven.util.heightF
import com.soldierofheaven.util.serialization.WeaponJsonConverter
import com.soldierofheaven.util.widthF
import com.soldierofheaven.weapons.BulletData
import com.soldierofheaven.weapons.Weapon
import ktx.app.KtxGame
import java.io.File

//todo: add camera shake :)
//todo: monitor performance of the game with the current amount of event dispatching; if it suffers, implement pooling or switch to callbacks
//todo: maybe add skills? e.g. summoning an angel to help (like those circles with eyes)
//todo: add weapon swap sound (similar to the one in nuclear throne maybe?)
//todo: download and set up LibGDX physics editor (to reduce boilerplate)
//todo: set up particle effect pool
//todo: add explosive type (missile should speed up, grenade should slow down over time etc.)
//todo: if setting up pools: use GDX pool implementations and inject them into systems using Artemis
//todo: explosive bullets should have both bullet and explosive components
//todo: explosives should explode on impact with enemies or marked props (e.g. explosive barrels or sth)
//todo: make object with all available tags
//todo: think about how to make explosive bullets (because current implementation does not support it)
class SoldierOfHeavenGame : KtxGame<Screen>() {
    private lateinit var batch: SpriteBatch
    private lateinit var physicsWorld: PhysicsWorld
    private lateinit var ecsWorld: EcsWorld
    private lateinit var camera: OrthographicCamera
    lateinit var assetManager: AssetManager

    override fun create() {
        Box2D.init()
        batch = SpriteBatch()
        camera = OrthographicCamera(Gdx.graphics.widthF(), Gdx.graphics.heightF())
        assetManager = AssetManager()
        // we do not need any gravity in this game
        physicsWorld = PhysicsWorld(Vector2.Zero, false)

        assetManager.load("gfx/crosshair.png", Texture::class.java)
        assetManager.load("gfx/reload-bar.png", Texture::class.java)
        assetManager.load("gfx/bullet-basic.png", Texture::class.java)
        assetManager.load("sfx/pistol-reload.wav", Sound::class.java)
        assetManager.load("sfx/pistol-shot.wav", Sound::class.java)
        assetManager.load("sfx/shotgun-shot.wav", Sound::class.java)
        assetManager.load("sfx/shotgun-reload.wav", Sound::class.java)
        assetManager.load("sfx/rifle-shot.wav", Sound::class.java)
        assetManager.load("sfx/rifle-reload.wav", Sound::class.java)
        assetManager.load("gfx/particles/explosion.particle", com.badlogic.gdx.graphics.g2d.ParticleEffect::class.java,
            ParticleEffectLoader.ParticleEffectParameter().apply {
                imagesDir = Gdx.files.internal("gfx/particles")
            })
        assetManager.load("gfx/particles/rocket-trail.particle", com.badlogic.gdx.graphics.g2d.ParticleEffect::class.java,
        ParticleEffectLoader.ParticleEffectParameter().apply {
            imagesDir = Gdx.files.internal("gfx/particles")
        })
        assetManager.finishLoading()

        ParticlePools.registerEffect("Rocket trail", assetManager.get("gfx/particles/rocket-trail.particle"), 5, 10)
        ParticlePools.registerEffect("Explosion", assetManager.get("gfx/particles/explosion.particle"), 10, 15)

        val ecsWorldConfig = WorldConfigurationBuilder().with(
            PhysicsSystem(physicsWorld),
            InputSystem(),
            AnimationSystem(),
            RenderSystem(),
            CameraPositioningSystem(camera),
            ParticleEffectSystem(),
            WeaponSystem(buildWeapons()),
            BulletSystem(),
            DamageSystem(),
            RemovalSystem()
        ).build()
        ecsWorldConfig.register("physicsWorld", physicsWorld)
        ecsWorldConfig.register("gameCamera", camera)
        ecsWorldConfig.register("mainBatch", batch)
        EventQueue.init(ecsWorldConfig)

        ecsWorld = EcsWorld(ecsWorldConfig)
        physicsWorld.setContactListener(GameContactListener(ecsWorld))

        addScreen(MenuScene(this))
        addScreen(GameScene(this, ecsWorld, physicsWorld))

        screens.forEach { e -> EventQueue.register(e.value) }

        setScreen<MenuScene>()
    }

    override fun dispose() {
        batch.dispose()
        physicsWorld.dispose()
        ecsWorld.dispose()
        assetManager.dispose()
    }

    //this should be removed before final build
    private fun buildWeapons(): List<Weapon> {
        val baseBulletSpeed = 1000f
        return ArrayList(listOf(
            Weapon("Peacemaker", 10, Weapon.INFINITE_AMMO, 1f, 10f,
                0.25f, -1, assetManager.get(Resources.BASIC_BULLET),
                assetManager.get(Resources.BASIC_BULLET),
                BulletData(baseBulletSpeed, 0f, null, null,
                    null, null, null, assetManager.get(Resources.BASIC_BULLET)),
                assetManager.get("sfx/pistol-shot.wav"),
                assetManager.get("sfx/pistol-reload.wav")).apply { unlocked = true },
            Weapon("The Absolver", 30, 600, 2.5f, 6f,
                0.1f, 800, assetManager.get(Resources.BASIC_BULLET),
                assetManager.get(Resources.BASIC_BULLET),
                BulletData(baseBulletSpeed, 0f, null, null,
                    null, null, null, assetManager.get(Resources.BASIC_BULLET)),
                assetManager.get("sfx/rifle-shot.wav"), assetManager.get("sfx/rifle-reload.wav")
            ).apply { unlocked = true },
            Weapon("Gate Guardian", 6, 100, 4.25f, 5f, 1.25f, 1200,
                assetManager.get(Resources.BASIC_BULLET), assetManager.get(Resources.BASIC_BULLET),
                BulletData(baseBulletSpeed, 0f, null, null,
                    null, null, null, assetManager.get(Resources.BASIC_BULLET)),
                assetManager.get("sfx/shotgun-shot.wav"), assetManager.get("sfx/shotgun-reload.wav"),
                bulletSpread = 0.25f, bulletsPerShot = 10
            ).apply { unlocked = true }
            //smg should have very high fire rate and also small bullet spread
//            Weapon("SMG PLACEHOLDER NAME", 45, 900, 2.5f, 5f, 0.05f, 2500,
//                assetManager.get(Resources.BASIC_BULLET), assetManager.get(Resources.BASIC_BULLET),
//                BulletData(baseBulletSpeed, 0f, null, null,
//                    null, null, null, assetManager.get(Resources.BASIC_BULLET)),
//                assetManager.get("sfx/smg-shot.wav"), assetManager.get("sfx/smg-reload.wav"), bulletSpread = 0.1f)
//            Weapon("ROCKET LAUNCHER PLACEHOLDER NAME", 3, 15, 4f, 75f, 2.5f, 5000,
//                assetManager.get(Resources.BASIC_BULLET), assetManager.get(Resources.BASIC_BULLET),
//                BulletData(baseBulletSpeed, assetManager.get(Resources.BASIC_BULLET)),
//                assetManager.get("sfx/rocket-launcher-shot.wav"), assetManager.get("sfx/rocket-launcher-reload.wav")
//            )
//            Weapon("PLACEHOLDER GRENADE_LAUNCHER NAME", 4, 40, 3.25f, 40f, 1f, 4000,
//                assetManager.get(Resources.BASIC_BULLET), assetManager.get(Resources.BASIC_BULLET),
//                BulletData(baseBulletSpeed, 5f, ExplosiveType.GRENADE, 50f, true,
//                    3f, "Explosion", assetManager.get(Resources.BASIC_BULLET)),
//                assetManager.get("sfx/grenade-launcher-shot.wav"), assetManager.get("sfx/grenade-launcher-shot.wav")
//            )
        ))
    }

    private fun dumpWeaponDefinitions() {
        val weaponDefs = buildWeapons()
        val converter = WeaponJsonConverter(assetManager)
        val json = converter.toJson(weaponDefs)
        File("weapon-defs.json").writeText(json, Charsets.UTF_8)
    }

    private fun loadWeaponDefs(): List<Weapon> {
        val handle = Gdx.files.internal("resources/weapon-defs.json")
        val text = handle.readString("UTF-8")
        return WeaponJsonConverter(assetManager).fromJson(text)
    }
}
