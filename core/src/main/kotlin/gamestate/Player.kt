package gamestate

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import ecs.components.gameplay.ObjectiveComponent

class Player() {
    lateinit var body: Body
    lateinit var entity: Entity
    val startingHealth = 100
    val startingLives = 3

    var lives = startingLives
    var health: Int = startingHealth
    set(value) {
        field = value.coerceAtLeast(0)
    }

    val touchedObjectives = mutableSetOf<ObjectiveComponent>()
    val isDead : Boolean
        get() = health < 1

    fun reset() {
        lives = startingLives
        health = startingHealth
        touchedObjectives.clear()
    }
}