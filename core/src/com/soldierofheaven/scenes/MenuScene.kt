package com.soldierofheaven.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.util.heightF
import com.soldierofheaven.util.widthF
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

        val controlsWindow = Window("Controls", defaultSkin)
        controlsWindow.isVisible = false
        controlsWindow.setSize(600f, 400f)
        controlsWindow.setPosition(Gdx.graphics.widthF() / 2 - controlsWindow.width / 2, Gdx.graphics.heightF() / 2 - controlsWindow.height / 2)
        val creditsWindow = Window("Credits", defaultSkin)
        creditsWindow.isVisible = false
        creditsWindow.setSize(600f, 400f)
        creditsWindow.setPosition(Gdx.graphics.widthF() / 2 - creditsWindow.width / 2, Gdx.graphics.heightF() / 2 - creditsWindow.height / 2)
        val returnButton = ImageButton(defaultSkin).apply { isVisible = false }
        returnButton.setSize(40f, 40f)
        returnButton.setPosition(5f, Gdx.graphics.heightF() - returnButton.height - 5f)
        stage.addActor(creditsWindow)
        stage.addActor(controlsWindow)
        stage.addActor(returnButton)

        val titleLabel = Label("Soldier of Heaven", defaultSkin)
        val howToPlayButton = ImageTextButton("How to play", defaultSkin)
        val aboutButton = ImageTextButton("About", defaultSkin)
        val optionsButton = ImageTextButton("Options", defaultSkin)
        val buttons = arrayOf(
            ImageTextButton("New Game", defaultSkin).apply {
                addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        game.setScreen<GameScene>()
                    }
                })
            },
            howToPlayButton,
            optionsButton,
            aboutButton,
            ImageTextButton("Exit", defaultSkin).apply {
                addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        Gdx.app.exit()
                        exitProcess(0)
                    }
                })
            }
        )

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

        howToPlayButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                controlsWindow.isVisible = true
                creditsWindow.isVisible = false
                buttons.forEach { it.isVisible = false }
                titleLabel.isVisible = false
                returnButton.isVisible = true
            }
        })
        aboutButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                controlsWindow.isVisible = false
                creditsWindow.isVisible = true
                buttons.forEach { it.isVisible = false }
                titleLabel.isVisible = false
                returnButton.isVisible = true
            }
        })
        //this has to be at the end to capture the state of above actors
        returnButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                controlsWindow.isVisible = false
                creditsWindow.isVisible = false
                buttons.forEach { it.isVisible = true }
                titleLabel.isVisible = true
                returnButton.isVisible = false
            }
        })
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
