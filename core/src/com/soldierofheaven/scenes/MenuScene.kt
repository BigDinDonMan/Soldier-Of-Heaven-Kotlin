package com.soldierofheaven.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.util.heightF
import com.soldierofheaven.util.widthF
import net.mostlyoriginal.api.event.common.Event
import net.mostlyoriginal.api.event.common.Subscribe

class MenuScene(private val game: SoldierOfHeavenGame) : ScreenAdapter() {

    private val viewport = StretchViewport(Gdx.graphics.widthF(), Gdx.graphics.heightF())
    private val stage = Stage(viewport)

    init {
        initUi()
    }

    private fun initUi() {
        val rootWidget = Table()
        rootWidget.setFillParent(true)
        rootWidget.setDebug(true)
    }

    @Subscribe
    private fun updateWeaponUi(e: Event){

    }

    override fun dispose() {
        stage.dispose()
    }

    override fun render(delta: Float) {
        super.render(delta)
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height)
}
