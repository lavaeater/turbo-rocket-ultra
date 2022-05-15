package ecs.components.gameplay

import data.Player
import ecs.components.ai.old.CoolDownComponent

class GrenadeComponent: CoolDownComponent() {
    lateinit var player: Player
    override fun reset() {
        coolDownRange = 3f..5f
        super.reset()
    }
}