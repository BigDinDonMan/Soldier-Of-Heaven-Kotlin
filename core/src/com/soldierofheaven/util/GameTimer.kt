package com.soldierofheaven.util

open class GameTimer(val time: Float, val looping: Boolean = false, val elapsedCallback: () -> Unit = {}) {
    private var counter = 0f
    private var started = false

    open fun update(delta: Float) {
        if (!started) return
        counter += delta
        if (counter >= time) {
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
}
