package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Camera

class CameraPositioningSystem : BaseSystem() {

    var gameCamera: Camera? = null
    var playerEntityId: Int? = null

    override fun processSystem() {

    }
}
