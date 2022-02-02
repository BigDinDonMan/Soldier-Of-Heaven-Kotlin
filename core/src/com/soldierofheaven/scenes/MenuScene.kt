package com.soldierofheaven.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.ui.HealthBar
import com.soldierofheaven.util.heightF
import com.soldierofheaven.util.widthF
import net.mostlyoriginal.api.event.common.Event
import net.mostlyoriginal.api.event.common.Subscribe

class MenuScene(private val game: SoldierOfHeavenGame) : ScreenAdapter() {

    private val viewport = StretchViewport(Gdx.graphics.widthF(), Gdx.graphics.heightF())
    private val stage = Stage(viewport)

    private val defaultSkin = Skin(Gdx.files.internal("skins/uiskin.json"))

//    private val healthBar = HealthBar(150, defaultSkin)

    init {
        initUi()
    }

    private fun initUi() {
//        val healthBarPadding = 10f
//        val height = 50f
//        val width = 250f
//        stage.addActor(healthBar.apply {
//            setPosition(healthBarPadding, Gdx.graphics.heightF() - healthBarPadding - height)
//            this.width = width
//            this.height = height
//        })
    }

    @Subscribe
    private fun updateWeaponUi(e: Event){

    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun dispose() {
        stage.dispose()
//        healthBar.dispose()
    }

    override fun render(delta: Float) {
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height)
}
