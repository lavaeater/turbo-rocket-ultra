package ecs.components.player

import ecs.components.ai.CoolDownComponent

class PlayerRespawning: CoolDownComponent() {
    init {
        coolDownRange = (5f..5f)
        reset()
    }
}