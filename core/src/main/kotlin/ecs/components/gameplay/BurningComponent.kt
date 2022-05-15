package ecs.components.gameplay

import data.Player
import ecs.components.ai.old.CoolDownComponent

class BurningComponent : CoolDownComponent() {
    lateinit var player: Player
    var damageRange = 5..15
    override fun reset() {
        //Burn for this amount of time
        coolDownRange = 3f..8f
        super.reset()
    }

    override fun toString(): String {
        return "Burning"
    }
}