package com.soldierofheaven.scenes

import com.badlogic.gdx.ScreenAdapter
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld

class GameScene(private val game: SoldierOfHeavenGame, private val ecsWorld: EcsWorld, private val physicsWorld: PhysicsWorld) : ScreenAdapter() {

    override fun dispose() {
        super.dispose()
    }

    override fun render(delta: Float) {
        super.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }
}
