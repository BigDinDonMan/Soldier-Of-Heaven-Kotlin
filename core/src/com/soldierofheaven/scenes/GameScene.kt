package com.soldierofheaven.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld
import com.soldierofheaven.util.heightF
import com.soldierofheaven.util.widthF

class GameScene(private val game: SoldierOfHeavenGame, private val ecsWorld: EcsWorld, private val physicsWorld: PhysicsWorld) : ScreenAdapter() {

    private val viewport = StretchViewport(Gdx.graphics.widthF(), Gdx.graphics.heightF())
    private val stage = Stage(viewport)

    override fun dispose() {
        stage.dispose()
    }

    override fun render(delta: Float) {
        super.render(delta)
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height)
}
