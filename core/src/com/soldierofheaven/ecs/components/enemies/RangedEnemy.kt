package com.soldierofheaven.ecs.components.enemies

import com.artemis.PooledComponent
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

class RangedEnemy : PooledComponent() {

    var playerPosition: Vector3? = null

    override fun reset() {

    }
}
