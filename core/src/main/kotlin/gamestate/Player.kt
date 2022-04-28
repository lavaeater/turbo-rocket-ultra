package gamestate

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import ecs.components.ObjectiveComponent

class Player(
    val body: Body,
    val entity: Entity,
    var health: Int = 100) {
    val touchedObjectives = mutableSetOf<ObjectiveComponent>()
    val dead : Boolean
        get() = health < 1
}