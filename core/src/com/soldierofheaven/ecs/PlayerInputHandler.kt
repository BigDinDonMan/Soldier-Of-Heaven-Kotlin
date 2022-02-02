package com.soldierofheaven.ecs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.soldierofheaven.ecs.events.MoveEvent
import com.soldierofheaven.ecs.events.ReloadEvent
import com.soldierofheaven.ecs.events.ShootEvent
import ktx.app.KtxInputAdapter
import net.mostlyoriginal.api.event.common.EventSystem

class PlayerInputHandler(private val eventBus: EventSystem) : KtxInputAdapter {
    private var enabled = true

    override fun keyDown(keycode: Int): Boolean {
        if (!enabled) return false
        var x = 0f
        var y = 0f
        when (keycode) {
            Input.Keys.W -> y = 1f
            Input.Keys.S -> y = -1f
            Input.Keys.A -> x = -1f
            Input.Keys.D -> x = 1f
        }

        eventBus.dispatch(MoveEvent(x, y))

        if (keycode == Input.Keys.R) {
            eventBus.dispatch(ReloadEvent())
        }

        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        if (!enabled) return false

        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!enabled) return false

        return if (button == Input.Buttons.LEFT){
            eventBus.dispatch(ShootEvent(true))
            true
        } else false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!enabled) return false

        return if (button == Input.Buttons.LEFT){
            eventBus.dispatch(ShootEvent(false))
            true
        } else false
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}
