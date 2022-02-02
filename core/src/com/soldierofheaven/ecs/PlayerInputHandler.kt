package com.soldierofheaven.ecs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.soldierofheaven.ecs.events.InputEvent
import ktx.app.KtxInputAdapter
import net.mostlyoriginal.api.event.common.EventSystem

class PlayerInputHandler(private val eventBus: EventSystem) : KtxInputAdapter {
    override fun keyDown(keycode: Int): Boolean {
        var x = 0f
        var y = 0f
        when (keycode) {
            Input.Keys.W -> y = 1f
            Input.Keys.S -> y = -1f
            Input.Keys.A -> x = -1f
            Input.Keys.D -> x = 1f
        }

        eventBus.dispatch(InputEvent(x, y, Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)))
//        eventSystem.dispatch(PlayerInputEvent(playerEntity, x, y, dodge = false, attack = false, interaction = false))

        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return true
    }
}
