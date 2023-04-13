package input

import com.badlogic.ashley.core.Entity
import data.Players
import eater.input.GamepadControl


fun GamepadControl.entityFor() : Entity {
    return Players.players.filter { it.key == this }.map { it.value.entity }.first()
}