package com.soldierofheaven

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils

class SoldierOfHeavenGame : Game() {
    private val batch: SpriteBatch = SpriteBatch()

    override fun create() {
    }

    override fun render() {
        ScreenUtils.clear(1f, 0f, 0f, 1f)
        batch.begin()
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }
}
