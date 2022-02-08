package com.soldierofheaven.ecs.systems

import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.soldierofheaven.ecs.components.Animation
import com.soldierofheaven.ecs.components.TextureDisplay

@All(TextureDisplay::class, Animation::class)
class AnimationSystem : IteratingSystem() {
    override fun process(entityId: Int) {

    }
}
