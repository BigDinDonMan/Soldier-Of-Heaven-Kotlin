package com.soldierofheaven.ecs.components.weapons

import com.artemis.Component

class Weapon : Component() {
    var fireRate = 0f
    var clipSize = 0
    var currentAmmo = 0
}
