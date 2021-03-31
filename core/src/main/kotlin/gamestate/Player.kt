package gamestate

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import ecs.components.gameplay.ObjectiveComponent

class Player() {
    lateinit var body: Body
    lateinit var entity: Entity
    var lives = 3
    var health: Int = 100
    set(value) {
        field = value.coerceAtLeast(0)
    }
    val touchedObjectives = mutableSetOf<ObjectiveComponent>()
    var respawning = false
    val isDead : Boolean
        get() = health < 1
}