package com.soldierofheaven.ai.sm.state

import com.artemis.ComponentMapper
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.soldierofheaven.ecs.components.Enemy
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Speed
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.applyImpulseToCenter
import com.soldierofheaven.util.math.euclideanDistance

//ugh this stinks but hey it works so
enum class EnemyState : StateAdapter<Enemy> {
    CHASING {
        override fun update(entity: Enemy) {
            val rigidBody = rigidBodyMapper.get(entity.ownerId)
            val speed = speedMapper.get(entity.ownerId)
            if (rigidBody?.physicsBody == null) return

            calculationVector.set(entity.playerPositionRef!!.x, entity.playerPositionRef!!.y).
                sub(rigidBody.physicsBody!!.position.x, rigidBody.physicsBody!!.position.y).
                nor()
            if (entity.isRanged) {
                val dist = euclideanDistance(rigidBody.physicsBody!!.position.x, rigidBody.physicsBody!!.position.y,
                    entity.playerPositionRef!!.x, entity.playerPositionRef!!.y)
                if (entity.runsAway) {
                    if (dist < entity.runAwayDistance!!){
                        entity.enemyStateMachine.changeState(RUNNING_AWAY)
                    } else {
                        if (dist < entity.shotStopRange!!) {
                            entity.enemyStateMachine.changeState(SHOOTING)
                        } else {
                            rigidBody.physicsBody!!.applyImpulseToCenter(
                                calculationVector.x * speed.value, calculationVector.y * speed.value, true
                            )
                        }
                    }
                } else {
                    if (dist < entity.shotStopRange!!) {
                        entity.enemyStateMachine.changeState(SHOOTING)
                    } else {
                        rigidBody.physicsBody!!.applyImpulseToCenter(
                            calculationVector.x * speed.value, calculationVector.y * speed.value, true
                        )
                    }
                }
            } else {
                rigidBody.physicsBody!!.applyImpulseToCenter(
                    calculationVector.x * speed.value, calculationVector.y * speed.value, true
                )
            }
        }
    },
    RUNNING_AWAY {
        override fun update(entity: Enemy) {
            entity.shotTimer?.update(Gdx.graphics.deltaTime)
            val rigidBody = rigidBodyMapper.get(entity.ownerId)
            val speed = speedMapper.get(entity.ownerId)
            if (rigidBody?.physicsBody == null) return

            calculationVector.set(rigidBody.physicsBody!!.position.x, rigidBody.physicsBody!!.position.y).
                sub(entity.playerPositionRef!!.x, entity.playerPositionRef!!.y).
                nor()
            val dist = euclideanDistance(entity.playerPositionRef!!.x, entity.playerPositionRef!!.y,
                rigidBody.physicsBody!!.position.x, rigidBody.physicsBody!!.position.y)
            if (dist > entity.runAwayDistance!!) {
                entity.enemyStateMachine.changeState(if (dist <= entity.shotStopRange!!) SHOOTING else CHASING)
            } else {
                rigidBody.physicsBody!!.applyImpulseToCenter(
                    calculationVector.x * speed.value, calculationVector.y * speed.value, true
                )
            }
        }
    },
    SHOOTING {
        override fun update(entity: Enemy) {
            val rigidBody = rigidBodyMapper.get(entity.ownerId)
            if (rigidBody?.physicsBody == null) return

            calculationVector.set(rigidBody.physicsBody!!.position.x, rigidBody.physicsBody!!.position.y).
                sub(entity.playerPositionRef!!.x, entity.playerPositionRef!!.y).
                nor()
            val dist = euclideanDistance(entity.playerPositionRef!!.x, entity.playerPositionRef!!.y,
                rigidBody.physicsBody!!.position.x, rigidBody.physicsBody!!.position.y)
            if (dist > entity.shotStopRange!!) {
                entity.enemyStateMachine.changeState(CHASING) }
            else if (entity.runsAway && dist <= entity.runAwayDistance!!) {
                entity.enemyStateMachine.changeState(RUNNING_AWAY)
            }
        }
    }
    ;

    companion object {
        private lateinit var ecsWorld: EcsWorld
        private lateinit var rigidBodyMapper: ComponentMapper<RigidBody>
        private lateinit var speedMapper: ComponentMapper<Speed>
        private val calculationVector = Vector2()

        fun init(ecsWorld: EcsWorld) {
            this.ecsWorld = ecsWorld
            rigidBodyMapper = ecsWorld.getMapper(RigidBody::class.java)
            speedMapper = ecsWorld.getMapper(Speed::class.java)
        }
    }
}
