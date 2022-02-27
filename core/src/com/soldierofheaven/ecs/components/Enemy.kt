package com.soldierofheaven.ecs.components

import com.artemis.PooledComponent
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.soldierofheaven.ai.sm.state.EnemyState

//todo: add a bullet prefab if it is a ranged enemy
//todo: seeking bullet (e.g. flaming skull chasing the player) could be made using an enemy that is destroyed on contact
class Enemy : PooledComponent() {

    var ownerId: Int = 0
    var playerPositionRef: Vector2? = null
    //if this is set to null then it is a melee enemy; otherwise its a range from player at which enemy stops and starts shooting
    var shotStopRange: Float? = null

    val enemyStateMachine = DefaultStateMachine<Enemy, EnemyState>(this, EnemyState.CHASING)

    val isRanged: Boolean
        get() = shotStopRange != null

    override fun reset() {
        shotStopRange = null
        playerPositionRef = null
    }
}
