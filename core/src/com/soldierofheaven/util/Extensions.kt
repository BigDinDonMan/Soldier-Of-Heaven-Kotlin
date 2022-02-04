package com.soldierofheaven.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import kotlin.math.abs

fun Graphics.widthF() = Gdx.graphics.width.toFloat()
fun Graphics.heightF() = Gdx.graphics.height.toFloat()

fun Double.isCloseTo(_val: Double, eps: Double = 0.0001) = abs(this - _val) <= eps
fun Float.isCloseTo(_val: Float, eps: Float = 0.0001f) = abs(this - _val) <= eps
