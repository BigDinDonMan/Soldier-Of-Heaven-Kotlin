package com.soldierofheaven.ecs.components.enemies

import com.artemis.PooledComponent
import com.badlogic.gdx.ai.fsm.StateMachine
import com.badlogic.gdx.math.Vector2

class BasicEnemy : PooledComponent() {

    var playerPosition: Vector2? = null

    override fun reset() {

    }
}
