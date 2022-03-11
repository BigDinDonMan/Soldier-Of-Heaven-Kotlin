package com.soldierofheaven.ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.soldierofheaven.ecs.components.Transform
import com.soldierofheaven.ecs.events.CameraShakeEvent
import com.soldierofheaven.util.nextFloat
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.random.Random

class CameraPositioningSystem(private val gameCamera: Camera) : BaseSystem() {

    var playerEntityId: Int = 0

    @Wire
    var transformMapper: ComponentMapper<Transform>? = null


    private val screenCoordsVector = Vector3()
    private val actualPosition = Vector3()
    private var shaking = false
    private var shakeCountDown = 0f
    private var shakeMagnitude = 0f

    @Subscribe
    private fun receiveShakeEvent(e: CameraShakeEvent) {
        shaking = true
        shakeCountDown = e.duration
        shakeMagnitude = e.strength
    }

    override fun begin() {
        //this will be needed for camera shake
        if (!shaking) {
            actualPosition.set(gameCamera.position)
        }
    }

    override fun processSystem() {
        if (shakeCountDown > 0f) {
            shakeCountDown -= world.delta
        } else shaking = false

        val transform = transformMapper!!.get(playerEntityId) ?: return

        val z = gameCamera.position.z
        val mx = Gdx.input.x.toFloat()
        val my = Gdx.input.y.toFloat()
        screenCoordsVector.set(mx, my, z)
        gameCamera.unproject(screenCoordsVector)

        actualPosition.set(
            (transform.position.x + screenCoordsVector.x) / 2,
            (transform.position.y + screenCoordsVector.y) / 2,
            z
        )

        if (shaking) {
            actualPosition.add(
                Random.nextFloat(-shakeMagnitude, shakeMagnitude),
                Random.nextFloat(-shakeMagnitude, shakeMagnitude),
                0f
            )
            shakeMagnitude = MathUtils.lerp(shakeMagnitude, 0f, 0.05f)
        }

        gameCamera.position.set(actualPosition)
    }
}
