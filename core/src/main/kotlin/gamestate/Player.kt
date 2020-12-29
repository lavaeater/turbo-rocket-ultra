package gamestate

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body

class Player(
    val body: Body,
    val entity: Entity,
    var health: Int = 100) {
    val dead : Boolean
        get() = health < 1
}