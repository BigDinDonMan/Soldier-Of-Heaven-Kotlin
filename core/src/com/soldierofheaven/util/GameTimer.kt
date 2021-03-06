package com.soldierofheaven.util

import com.soldierofheaven.util.`interface`.Resettable

open class GameTimer(val time: Float, val looping: Boolean = false, val elapsedCallback: () -> Unit = {}) : Resettable {
    private var counter = 0f
    private var started = false
    val timeElapsed: Float
        get() = counter

    open fun update(delta: Float) {
        if (!started) return
        counter += delta
        if (counter >= time) {
            elapsedCallback.invoke()
            if (looping) {
                counter = 0f
            } else {
                stop()
            }
        }
    }

    fun start() {
        started = true
        counter = 0f
    }

    fun stop() {
        started = false
    }

    fun resume() {
        started = true
    }

    fun isRunning() = started

    override fun reset() {
        started = false
        counter = 0f
    }
}
