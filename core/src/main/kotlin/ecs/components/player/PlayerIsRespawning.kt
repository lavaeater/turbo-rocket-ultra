package ecs.components.player

import ecs.components.ai.old.CoolDownComponent

class PlayerIsRespawning: CoolDownComponent() {
    init {
        coolDownRange = (5f..5f)
        reset()
    }
}