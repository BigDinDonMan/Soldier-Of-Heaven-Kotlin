package com.soldierofheaven.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.soldierofheaven.SoldierOfHeavenGame
import com.soldierofheaven.util.*
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
        setupControlsWindow(controlsWindow)
        controlsWindow.centerAbsolute()
        val creditsWindow = Window("Credits", defaultSkin)
        creditsWindow.isVisible = false
        creditsWindow.setSize(600f, 400f)
        setupAboutWindow(creditsWindow)
        creditsWindow.centerAbsolute()
        val returnButton = ImageButton(defaultSkin).apply { isVisible = false }
        returnButton.setSize(40f, 40f)
        returnButton.setPosition(5f, Gdx.graphics.heightF() - returnButton.height - 5f)
        val optionsWindow = Window("Options", defaultSkin)
        optionsWindow.isVisible = false
        optionsWindow.setSize(700f, 450f)
        setupOptionsWindow(optionsWindow)
        optionsWindow.centerAbsolute()
        stage.addActors(creditsWindow, controlsWindow, optionsWindow, returnButton)

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
                optionsWindow.isVisible = false
                buttons.forEach { it.isVisible = false }
                titleLabel.isVisible = false
                returnButton.isVisible = true
            }
        })
        aboutButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                controlsWindow.isVisible = false
                optionsWindow.isVisible = false
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
                optionsWindow.isVisible = false
                buttons.forEach { it.isVisible = true }
                titleLabel.isVisible = true
                returnButton.isVisible = false
            }
        })
        optionsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                controlsWindow.isVisible = false
                optionsWindow.isVisible = true
                creditsWindow.isVisible = false
                buttons.forEach { it.isVisible = false }
                titleLabel.isVisible = false
                returnButton.isVisible = true
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
        stage.update()
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height)

    private fun setupControlsWindow(window: Window) {
        //use two tables to create 2 columns if controls cannot fit in one column
        val leftSideTable = Table()
        val rightSideTable = Table()
        val parentTable = Table()
        val scroll = ScrollPane(parentTable)
        parentTable.add(leftSideTable).left()
        parentTable.add(rightSideTable).right()
        window.add(scroll)
        leftSideTable.row().left()
        leftSideTable.add(Image())
        leftSideTable.add(Label("Move around", defaultSkin))
        leftSideTable.row().left()
        leftSideTable.add(Image())
        leftSideTable.add(Label("Reload", defaultSkin))
        leftSideTable.row().left()
        leftSideTable.add(Image())
        leftSideTable.add(Label("Shoot", defaultSkin))
        leftSideTable.row().left()
        leftSideTable.add(Image())
        leftSideTable.add(Label("Swap weapons", defaultSkin))
    }

    private fun setupOptionsWindow(window: Window) {}
    private fun setupAboutWindow(window: Window) {}
}
