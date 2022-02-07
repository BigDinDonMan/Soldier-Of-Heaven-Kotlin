package com.soldierofheaven.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.ecs.events.WeaponChangeEvent
import com.soldierofheaven.util.heightF
import com.soldierofheaven.util.widthF
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.system.exitProcess

class MenuScene(private val game: SoldierOfHeavenGame) : ScreenAdapter() {

    private val viewport = StretchViewport(Gdx.graphics.widthF(), Gdx.graphics.heightF())
    private val stage = Stage(viewport)

    private val defaultSkin = Skin(Gdx.files.internal("skins/uiskin.json"))

    init {
        initUi()
    }

    private fun initUi() {
        val rootTable = Table().apply { setFillParent(true) }
        stage.addActor(rootTable)

        //todo: add a window with "how to play" instructions and show it on button click (or maybe a separate scene?)
        //todo: add return button
        //todo: add "about" window with author and game (?) info

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

        val returnButton = ImageButton(defaultSkin).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    //todo: switch back to main menu (hide dialog windows)
                }
            })
        }

        val howToPlayGroup = Group()
        val aboutGroup = Group()

        val titlePadding = 75f

        rootTable.center().top()
        rootTable.add(titleLabel).center().top().padTop(titlePadding).padBottom(titlePadding)

        val padding = 25f
        val buttonWidth = 250f
        val buttonHeight = 55f
        for (button in buttons) {
            rootTable.row()
            rootTable.add(button).center().top().padBottom(padding).padTop(padding).width(buttonWidth).height(buttonHeight)
        }
    }

    @Subscribe
    private fun updateWeaponUi(e: WeaponChangeEvent){

    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun render(delta: Float) {
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height)
}
