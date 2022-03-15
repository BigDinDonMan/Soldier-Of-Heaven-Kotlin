package com.soldierofheaven.util.physics

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.RayCastCallback
import com.soldierofheaven.ecs.components.Tag
import com.soldierofheaven.util.EcsWorld

//tag filters is a list of HITTABLE tags
class GenericRayCastCallback(ecsWorld: EcsWorld, vararg tagFilters: String) : RayCastCallback {
    val hitResults = ArrayList<Int>()
    private val filters = listOf(*tagFilters)
    private val tagMapper = ecsWorld.getMapper(Tag::class.java)

    override fun reportRayFixture(fixture: Fixture, point: Vector2, normal: Vector2, fraction: Float): Float {
        val entityId = fixture.body.userData as? Int ?: return 1f
        val tag = tagMapper.get(entityId)
        if (tag.value in filters) {
            hitResults += entityId
        }
        return 1f
    }
}
