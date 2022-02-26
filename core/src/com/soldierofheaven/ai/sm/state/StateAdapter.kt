package com.soldierofheaven.ai.sm.state

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram

interface StateAdapter<E> : State<E> {
    override fun enter(entity: E) {
    }

    override fun update(entity: E) {
    }

    override fun exit(entity: E) {
    }

    override fun onMessage(entity: E, telegram: Telegram?): Boolean {
        return false
    }
}
