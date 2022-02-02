package com.soldierofheaven.desktop

import kotlin.jvm.JvmStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.soldierofheaven.SoldierOfHeavenGame

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration().apply {
            title = "Soldier of Heaven"
            width = 1024
            height = 768
        }
        LwjglApplication(SoldierOfHeavenGame(), config)
    }
}
