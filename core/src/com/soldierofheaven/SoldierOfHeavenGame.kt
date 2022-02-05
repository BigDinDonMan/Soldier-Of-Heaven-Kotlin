package com.soldierofheaven

import com.artemis.WorldConfigurationBuilder
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.soldierofheaven.ecs.components.Bullet
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

//todo: monitor performance of the game with the current amount of event dispatching; if it suffers, implement pooling or switch to callbacks
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
        assetManager.finishLoading()

        val ecsWorldConfig = WorldConfigurationBuilder().with(
            PhysicsSystem(physicsWorld),
            InputSystem(),
            CameraPositioningSystem(camera),
            RenderSystem(batch, camera),
            WeaponSystem(loadWeaponDefs()),
            BulletSystem(),
            RemovalSystem()
        ).build()
        ecsWorldConfig.register("physicsWorld", physicsWorld)
        ecsWorldConfig.register("gameCamera", camera)
        EventQueue.init(ecsWorldConfig)

        ecsWorld = EcsWorld(ecsWorldConfig)

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
        return ArrayList(listOf(
            Weapon("Peacemaker", 10, Weapon.INFINITE_AMMO, 1f, 10f,
                0.25f, -1, assetManager.get("gfx/bullet-basic.png"),
                BulletData(5f, 10f, assetManager.get("gfx/bullet-basic.png")),
                assetManager.get("sfx/pistol-shot.wav"),
                assetManager.get("sfx/pistol-reload.wav")).apply { unlocked = true }
        ))
    }

    private fun dumpWeaponDefinitions() {
        val weaponDefs = buildWeapons()
        val converter = WeaponJsonConverter(assetManager)
        val json = converter.toJson(weaponDefs)
        File("weapon-defs.json").writeText(json, Charsets.UTF_8)
    }

    private fun loadWeaponDefs(): List<Weapon> {
        val text = Gdx.files.internal("weapon-defs.json").file().readText(Charsets.UTF_8)
        return WeaponJsonConverter(assetManager).fromJson(text)
    }
}
