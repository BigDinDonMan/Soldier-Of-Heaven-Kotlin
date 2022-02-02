package com.soldierofheaven.ecs.components

import com.artemis.Component
import com.badlogic.gdx.physics.box2d.Body

class RigidBody : Component() {
    var physicsBody: Body? = null
}
