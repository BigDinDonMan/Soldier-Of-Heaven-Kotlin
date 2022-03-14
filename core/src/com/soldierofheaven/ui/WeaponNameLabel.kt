package com.soldierofheaven.ui

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.soldierofheaven.weapons.Weapon


class WeaponNameLabel(var playerPositionRef: Vector2, skin: Skin) : Label("", skin) {
    override fun act(delta: Float) {
        super.act(delta)

        setPosition(playerPositionRef.x, playerPositionRef.y + 60f, Align.center)
    }

    fun update(w: Weapon) {
        clearActions()
        setText(w.name)
        setColor(color.r, color.g, color.b, 1f)
        addAction(Actions.sequence(
            Actions.delay(0.33f),
            Actions.fadeOut(0.25f)
        ))
        pack()
    }
}
