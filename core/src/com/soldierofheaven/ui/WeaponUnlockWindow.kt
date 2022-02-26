package com.soldierofheaven.ui

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.soldierofheaven.weapons.Weapon

class WeaponUnlockWindow(private val weapons: List<Weapon>, skin: Skin) : Window("", skin) {

    init {
        initUi()
    }

    private fun initUi() {}
}
