package com.soldierofheaven.ai.sm.state

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.soldierofheaven.ecs.components.Enemy

enum class EnemyState : StateAdapter<Enemy> {
    CHASING {
        override fun update(entity: Enemy) {
            super.update(entity)
        }
    },
    RUNNING_AWAY {
        override fun update(entity: Enemy) {
            super.update(entity)
        }
    },
    SHOOTING {
        override fun update(entity: Enemy) {
            super.update(entity)
        }
    }
    ;
}
