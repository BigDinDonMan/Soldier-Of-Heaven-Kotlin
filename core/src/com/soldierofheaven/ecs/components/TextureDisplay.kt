package com.soldierofheaven.ecs.components

import com.artemis.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class TextureDisplay : Component() {
    val region = TextureRegion()
    var texture: Texture? = null
        set(value) {
            field = value
            if (field != null) {
                region.setRegion(field)
            }
        }
    var color: Color = Color.WHITE
}
