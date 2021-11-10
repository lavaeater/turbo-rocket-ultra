package ecs.components.enemy

import ecs.components.ai.CoolDownComponent

class EnemySpawnerComponent : CoolDownComponent() {
    init {
        coolDownRange = (0.01f..0.1f)
        reset()
    }
}