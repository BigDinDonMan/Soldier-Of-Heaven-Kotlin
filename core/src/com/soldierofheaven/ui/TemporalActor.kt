package com.soldierofheaven.ui

import com.badlogic.gdx.scenes.scene2d.Actor

//class for actors that only last a specific amount of time
abstract class TemporalActor(protected val duration: Float /*seconds*/) : Actor() {
}
