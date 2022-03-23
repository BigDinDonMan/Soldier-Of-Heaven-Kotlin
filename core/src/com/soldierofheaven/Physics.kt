package com.soldierofheaven

import com.artemis.ComponentMapper
import com.badlogic.gdx.physics.box2d.*
import com.soldierofheaven.ecs.components.RigidBody
import com.soldierofheaven.ecs.components.Tag
import com.soldierofheaven.ecs.systems.PhysicsSystem
import com.soldierofheaven.util.EcsWorld
import com.soldierofheaven.util.PhysicsWorld
import com.soldierofheaven.util.math.euclideanDistance
import java.util.*

object Physics {

    private lateinit var physicsWorld: PhysicsWorld
    private lateinit var ecsWorld: EcsWorld
    private lateinit var rigidbodyMapper: ComponentMapper<RigidBody>
    private lateinit var tagMapper: ComponentMapper<Tag>

    fun init(physicsWorld: PhysicsWorld, ecsWorld: EcsWorld) {
        this.physicsWorld = physicsWorld
        this.ecsWorld = ecsWorld
        rigidbodyMapper = ecsWorld.getMapper(RigidBody::class.java)
        tagMapper = ecsWorld.getMapper(Tag::class.java)
    }

    //gathers all physics entity ids captured inside the sphere and returns it as a list
    fun overlapSphere(x: Float, y: Float, radius: Float, vararg ignoreTags: String): List<Int> {
        val result = LinkedList<Int>()
        val ids = ecsWorld.getSystem(PhysicsSystem::class.java).entityIds
        for (i in 0 until ids.size()) {
            val id = ids.get(i)
            val rigidBody = rigidbodyMapper.get(id)
            if (rigidBody?.physicsBody == null) continue

            val tag = tagMapper.get(rigidBody.physicsBody!!.userData as Int)
            if (tag.value in ignoreTags) continue
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
    fun overlapSphereNonAlloc(x: Float, y: Float, radius: Float, outputArray: IntArray, vararg ignoreTags: String): Int {
        var currentIndex = 0
        val ids = ecsWorld.getSystem(PhysicsSystem::class.java).entityIds
        for (i in 0 until ids.size()) {
            if (outputArray.size < currentIndex) break
            val id = ids.get(i)
            val rigidBody = rigidbodyMapper.get(id)
            if (rigidBody?.physicsBody == null) continue

            val tag = tagMapper.get(rigidBody.physicsBody!!.userData as Int)
            if (tag.value in ignoreTags) continue

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

    fun newBoxBody(ownerId: Int, width: Float, height: Float, gravityScale: Float = 1f, bodyType: BodyDef.BodyType = BodyDef.BodyType.DynamicBody, bullet: Boolean = false,
                   linearDamping: Float = 0f, active: Boolean = true, angularDamping: Float = 0f, fixedRotation: Boolean = false,
                   friction: Float = 0.2f, isSensor: Boolean = false, density: Float = 0f, restitution: Float = 0f): Body {
        val shape = PolygonShape().apply { setAsBox(width / 2, height / 2) }
        return newBodyFromShape(ownerId, shape, gravityScale, bodyType, bullet, linearDamping, active,
                                angularDamping, fixedRotation, friction, isSensor, density, restitution)
                                .also { shape.dispose() }
    }

    fun newSquareBody(ownerId: Int, size: Float, gravityScale: Float = 1f, bodyType: BodyDef.BodyType = BodyDef.BodyType.DynamicBody, bullet: Boolean = false,
                      linearDamping: Float = 0f, active: Boolean = true, angularDamping: Float = 0f, fixedRotation: Boolean = false,
                      friction: Float = 0.2f, isSensor: Boolean = false, density: Float = 0f, restitution: Float = 0f): Body {
        val mid = size / 2f
        val shape = PolygonShape().apply { setAsBox(mid, mid) }
        return newBodyFromShape(ownerId, shape, gravityScale, bodyType, bullet, linearDamping, active,
            angularDamping, fixedRotation, friction, isSensor, density, restitution)
            .also { shape.dispose() }
    }

    fun newCircleBody(ownerId: Int, radius: Float, gravityScale: Float = 1f, bodyType: BodyDef.BodyType = BodyDef.BodyType.DynamicBody, bullet: Boolean = false,
                      linearDamping: Float = 0f, active: Boolean = true, angularDamping: Float = 0f, fixedRotation: Boolean = false,
                      friction: Float = 0.2f, isSensor: Boolean = false, density: Float = 0f, restitution: Float = 0f): Body {
        val shape = CircleShape().apply { this.radius = radius }
        return newBodyFromShape(ownerId, shape, gravityScale, bodyType, bullet, linearDamping, active,
            angularDamping, fixedRotation, friction, isSensor, density, restitution)
            .also { shape.dispose() }
    }

    private fun newBodyFromShape(ownerId: Int, bodyShape: Shape, gravityScale: Float = 1f, bodyType: BodyDef.BodyType = BodyDef.BodyType.DynamicBody, bullet: Boolean = false,
                                 linearDamping: Float = 0f, active: Boolean = true, angularDamping: Float = 0f, fixedRotation: Boolean = false,
                                 friction: Float = 0.2f, isSensor: Boolean = false, density: Float = 0f, restitution: Float = 0f): Body {
        val bodyDef = BodyDef().apply {
            this.gravityScale = gravityScale
            this.type = bodyType
            this.bullet = bullet
            this.linearDamping = linearDamping
            this.active = active
            this.angularDamping = angularDamping
            this.fixedRotation = fixedRotation
        }

        val fixtureDef = FixtureDef().apply {
            this.shape = bodyShape
            this.friction = friction
            this.isSensor = isSensor
            this.density = density
            this.restitution = restitution
        }

        return physicsWorld.createBody(bodyDef).apply {
            createFixture(fixtureDef)
            userData = ownerId
        }
    }
}
