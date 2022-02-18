package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import com.soldierofheaven.ecs.components.Transform

class CameraPositioningSystem(private val gameCamera: Camera) : BaseSystem() {

    var playerEntityId: Int = 0

    @Wire
    var transformMapper: ComponentMapper<Transform>? = null

    private val screenCoordsVector = Vector3()

    private var shaking = false

    override fun begin() {
        //this will be needed for camera shake
    }

    override fun processSystem() {
        val transform = transformMapper!!.get(playerEntityId)

        val z = gameCamera.position.z
        val mx = Gdx.input.x.toFloat()
        val my = Gdx.input.y.toFloat()
        screenCoordsVector.set(mx, my, z)
        gameCamera.unproject(screenCoordsVector)
        gameCamera.position.set(
            (transform.position.x + screenCoordsVector.x) / 2,
            (transform.position.y + screenCoordsVector.y) / 2,
            z
        )
    }

    override fun end() {
        //this will be needed for camera shake
    }
}
