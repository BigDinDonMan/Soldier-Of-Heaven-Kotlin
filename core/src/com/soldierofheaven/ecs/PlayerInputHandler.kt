package com.soldierofheaven.ecs

import com.badlogic.gdx.Input
import com.soldierofheaven.ecs.events.MoveEvent
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

        eventBus.dispatch(MoveEvent(x, y))

        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return super.touchUp(screenX, screenY, pointer, button)
    }
}
