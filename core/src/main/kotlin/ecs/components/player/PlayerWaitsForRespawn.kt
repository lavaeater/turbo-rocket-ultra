package ecs.components.player

import ecs.components.ai.CoolDownComponent

class PlayerWaitsForRespawn: CoolDownComponent() {
    init {
        coolDownRange = (10f..10f)
        reset()
    }
}