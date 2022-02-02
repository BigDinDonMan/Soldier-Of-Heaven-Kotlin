package com.soldierofheaven.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.ui.HealthBar
import com.soldierofheaven.util.heightF
import com.soldierofheaven.util.widthF
import net.mostlyoriginal.api.event.common.Event
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.system.exitProcess

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

        val rootTable = Table().apply { setFillParent(true) }
        stage.addActor(rootTable)

        val titleLabel = Label("Soldier of Heaven", defaultSkin)
        val buttons = arrayOf(
            ImageTextButton("New Game", defaultSkin).apply {
                addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        game.setScreen<GameScene>()
                    }
                })
            },
            ImageTextButton("How to play", defaultSkin),
            ImageTextButton("About", defaultSkin),
            ImageTextButton("Exit", defaultSkin).apply {
                addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        Gdx.app.exit()
                        exitProcess(0)
                    }
                })
            }
        )

        //todo: set buttons to have fixed width and height

        val titlePadding = 75f

        rootTable.center().top()
        rootTable.add(titleLabel).center().top().padTop(titlePadding).padBottom(titlePadding)

        val padding = 20f
        val buttonWidth = 250f
        val buttonHeight = 50f
        for (button in buttons) {
            rootTable.row()
            rootTable.add(button).center().top().padBottom(padding).padTop(padding).width(buttonWidth).height(buttonHeight)
        }
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
