package com.soldierofheaven.util

import com.artemis.Aspect
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import kotlin.math.abs
import kotlin.random.Random

fun Graphics.widthF() = this.width.toFloat()
fun Graphics.heightF() = this.height.toFloat()

fun Double.isCloseTo(_val: Double, eps: Double = 0.0001) = abs(this - _val) <= eps
fun Float.isCloseTo(_val: Float, eps: Float = 0.0001f) = abs(this - _val) <= eps

fun Stage.addActors(vararg actors: Actor) = actors.forEach(this::addActor)
fun Stage.update() = this.act().also { this.draw() }
fun Actor.centerAbsolute() = this.setPosition(Gdx.graphics.widthF() / 2 - width / 2, Gdx.graphics.heightF() / 2 - height / 2)

fun EcsWorld.update(delta: Float) = this.setDelta(delta).also { process() }
fun EcsWorld.removeAllEntities(removalCallback: (Int) -> Unit = {_ ->}) {
    val ids = this.aspectSubscriptionManager.get(Aspect.all()).entities
    for (i in 0 until ids.size()) {
        val id = ids.get(i)
        removalCallback.invoke(id)
        this.delete(id)
    }
}

fun Body.applyImpulseToCenter(impulseX: Float, impulseY: Float, wake: Boolean) {
    this.applyLinearImpulse(impulseX, impulseY, this.position.x, this.position.y, wake)
}

fun Random.nextFloat(from: Float, until: Float): Float {
    return from + this.nextFloat() * (until - from)
}

fun ShapeRenderer.roundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float) {
    this.rect(x + radius, y + radius, width - radius * 2, height - radius * 2)

    this.rect(x + radius, y, width - 2 * radius, radius)
    this.rect(x + width - radius, y + radius, radius, height - 2*radius)
    this.rect(x + radius, y + height - radius, width - 2*radius, radius)
    this.rect(x + radius, y + height - radius, width - 2*radius, radius)

    this.arc(x + radius, y + radius, radius, 180f, 90f)
    this.arc(x + width - radius, y + radius, radius, 270f, 90f)
    this.arc(x + width - radius, y + height - radius, radius, 0f, 90f)
    this.arc(x + radius, y + height - radius, radius, 90f, 90f)
}
