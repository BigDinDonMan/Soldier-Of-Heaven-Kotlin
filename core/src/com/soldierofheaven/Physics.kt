package com.soldierofheaven

import com.artemis.ComponentMapper
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.systems.PhysicsSystem
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld
import com.soldierofheaven.util.math.euclideanDistance
import java.util.*

object Physics {

    private lateinit var physicsWorld: PhysicsWorld
    private lateinit var ecsWorld: EcsWorld
    private lateinit var rigidbodyMapper: ComponentMapper<RigidBody>

    fun init(physicsWorld: PhysicsWorld, ecsWorld: EcsWorld) {
        this.physicsWorld = physicsWorld
        this.ecsWorld = ecsWorld
        rigidbodyMapper = ecsWorld.getMapper(RigidBody::class.java)
    }

    //gathers all physics entity ids captured inside the sphere and returns it as a list
    fun overlapSphere(x: Float, y: Float, radius: Float): List<Int> {
        val result = LinkedList<Int>()
        val ids = ecsWorld.getSystem(PhysicsSystem::class.java).entityIds
        for (i in 0 until ids.size()) {
            val id = ids.get(i)
            val rigidBody = rigidbodyMapper.get(id)
            if (rigidBody?.physicsBody == null) continue

            val bodyDistance = euclideanDistance(x, y, rigidBody.physicsBody!!.position.x, rigidBody.physicsBody!!.position.y)
            for (fixture in rigidBody.physicsBody!!.fixtureList) {
                val fixRadius = fixture.shape.radius //halleluyah it has a radius
                if (bodyDistance - fixRadius <= radius) {
                    result.add(id)
                    break
                }
            }
        }
        return result
    }

    //same as the above, but does not allocate a buffer, returns number of captured objects, and needs an output array as a parameter
    fun overlapSphereNonAlloc(x: Float, y: Float, radius: Float, outputArray: IntArray): Int {
        var currentIndex = 0
        val ids = ecsWorld.getSystem(PhysicsSystem::class.java).entityIds
        for (i in 0 until ids.size()) {
            if (outputArray.size < currentIndex) break
            val id = ids.get(i)
            val rigidBody = rigidbodyMapper.get(id)
            if (rigidBody?.physicsBody == null) continue

            val bodyDistance = euclideanDistance(x, y, rigidBody.physicsBody!!.position.x, rigidBody.physicsBody!!.position.y)
            for (fixture in rigidBody.physicsBody!!.fixtureList) {
                val fixRadius = fixture.shape.radius
                if (bodyDistance - fixRadius <= radius) {
                    outputArray[currentIndex++] = id
                    break
                }
            }
        }
        return currentIndex
    }
}
