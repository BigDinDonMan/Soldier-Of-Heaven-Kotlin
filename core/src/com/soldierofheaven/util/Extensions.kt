package com.soldierofheaven.util

import com.artemis.Aspect
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import kotlin.math.abs

fun Graphics.widthF() = Gdx.graphics.width.toFloat()
fun Graphics.heightF() = Gdx.graphics.height.toFloat()

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
