package com.soldierofheaven.ecs.components

import com.artemis.Component

class Player : Component() {
    var maxHealth = 150
    var health = maxHealth
    var speed = 10
}
