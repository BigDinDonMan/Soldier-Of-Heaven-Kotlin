package com.soldierofheaven.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.math.abs
import kotlin.math.ceil

//todo: add skin/font here
class CountingLabel(val prefix: String, startingValue: Int): Actor() {
    private val font: BitmapFont = BitmapFont()
    private var animating = false
    private var currentValue = startingValue.toFloat()
    private var targetValue = 0f
    private var increasePerTick = 0f
    private var speedCoef = 50;

    //todo: add colors (e.g. increase to green to animation half-point and then back to starting color)
    override fun act(delta: Float) {
        super.act(delta)
        if (animating) {
            currentValue += increasePerTick
            if (increasePerTick < 0f && currentValue <= targetValue) {
                currentValue = targetValue
                animating = false
            } else if (currentValue >= targetValue) {
                currentValue = targetValue
                animating = false
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        val prefix = if (this.prefix.isBlank()) "" else "${this.prefix}: "
        font.draw(batch, "${prefix}${ceil(currentValue).toInt()}", x, y)
    }

    fun startCountdown(increaseBy: Int) {
        animating = true
        this.targetValue = currentValue + increaseBy

        val direction = if (targetValue < currentValue) -1 else 1

        val diff = abs(targetValue - currentValue)
        val perTick = diff / speedCoef

        increasePerTick = perTick * direction
    }

    fun startCountdown(from: Int, to: Int) {
        startCountdown(to - from)
    }
}
