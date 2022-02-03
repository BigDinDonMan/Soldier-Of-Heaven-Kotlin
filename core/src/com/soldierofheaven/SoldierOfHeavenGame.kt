package com.soldierofheaven

import com.artemis.WorldConfiguration
import com.artemis.WorldConfigurationBuilder
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.utils.ScreenUtils
import com.soldierofheaven.ecs.systems.CameraPositioningSystem
import com.soldierofheaven.ecs.systems.InputSystem
import com.soldierofheaven.ecs.systems.PhysicsSystem
import com.soldierofheaven.ecs.systems.RenderSystem
import com.soldierofheaven.scenes.GameScene
import com.soldierofheaven.scenes.MenuScene
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld
import com.soldierofheaven.util.heightF
import com.soldierofheaven.util.widthF
import ktx.app.KtxGame
import net.mostlyoriginal.api.event.common.EventSystem

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

        val ecsWorldConfig = WorldConfigurationBuilder().with(
            PhysicsSystem(physicsWorld),
            InputSystem(),
            CameraPositioningSystem(camera),
            RenderSystem(batch, camera)
        ).build()
        ecsWorld = EcsWorld(ecsWorldConfig)

        assetManager.load("gfx/crosshair.png", Texture::class.java)
        assetManager.load("gfx/bullet-basic.png", Texture::class.java)
        assetManager.load("sfx/pistol-reload.wav", Sound::class.java)
        assetManager.load("sfx/pistol-shot.wav", Sound::class.java)
        assetManager.load("sfx/shotgun-shot.wav", Sound::class.java)
        assetManager.load("sfx/shotgun-reload.wav", Sound::class.java)
        assetManager.load("sfx/rifle-shot.wav", Sound::class.java)
        assetManager.load("sfx/rifle-reload.wav", Sound::class.java)
        assetManager.finishLoading()

        addScreen(MenuScene(this))
        addScreen(GameScene(this, ecsWorld, physicsWorld))

        setScreen<MenuScene>()
    }

    override fun dispose() {
        batch.dispose()
        physicsWorld.dispose()
        ecsWorld.dispose()
        assetManager.dispose()
    }
}
