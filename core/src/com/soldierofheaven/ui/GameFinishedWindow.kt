package com.soldierofheaven.ui

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.soldierofheaven.stats.StatisticsTracker

class GameFinishedWindow(private val statTrack: StatisticsTracker, skin: Skin) : Window("", skin) {
    init {
        initUi()
    }

    private fun initUi() {
        val retryButton = ImageTextButton("Retry", skin)
        val exitButton = ImageTextButton("Exit", skin)
    }
}
