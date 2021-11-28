package ecs.components.enemy

import ecs.components.ai.CoolDownComponent

class EnemySpawnerComponent : CoolDownComponent() {
    init {
        coolDownRange = (0.1f..0.2f)
        reset()
    }
}