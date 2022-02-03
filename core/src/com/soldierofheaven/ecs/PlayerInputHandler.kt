package com.soldierofheaven.ecs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.soldierofheaven.EventQueue
import com.soldierofheaven.ecs.events.MoveEvent
import com.soldierofheaven.ecs.events.ReloadRequestEvent
import com.soldierofheaven.ecs.events.ShotRequestEvent
import com.soldierofheaven.ecs.events.WeaponChangeEvent
import ktx.app.KtxInputAdapter

class PlayerInputHandler() : KtxInputAdapter {
    private var enabled = true

    private var x = 0f
    private var y = 0f

    override fun keyDown(keycode: Int): Boolean {
        if (!enabled) return false

        when (keycode) {
            Input.Keys.W -> y = 1f
            Input.Keys.S -> y = -1f
            Input.Keys.A -> x = -1f
            Input.Keys.D -> x = 1f
        }

        EventQueue.dispatch(MoveEvent(x, y))

        if (keycode == Input.Keys.R) {
            EventQueue.dispatch(ReloadRequestEvent())
        }

        if (keycode in (Input.Keys.NUM_1..Input.Keys.NUM_9)) {
            // subtracting like this will give us actual index
            EventQueue.dispatch(WeaponChangeEvent(keycode - Input.Keys.NUM_0))
        }

        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        if (!enabled) return false

        when (keycode) {
            Input.Keys.W -> y = if (Gdx.input.isKeyPressed(Input.Keys.S)) y else 0f
            Input.Keys.S -> y = if (Gdx.input.isKeyPressed(Input.Keys.W)) y else 0f
            Input.Keys.A -> x = if (Gdx.input.isKeyPressed(Input.Keys.D)) x else 0f
            Input.Keys.D -> x = if (Gdx.input.isKeyPressed(Input.Keys.A)) x else 0f
        }

        EventQueue.dispatch(MoveEvent(x,y))

        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!enabled) return false

        return if (button == Input.Buttons.LEFT){
            EventQueue.dispatch(ShotRequestEvent(true))
            true
        } else false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!enabled) return false

        return if (button == Input.Buttons.LEFT){
            EventQueue.dispatch(ShotRequestEvent(false))
            true
        } else false
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}
