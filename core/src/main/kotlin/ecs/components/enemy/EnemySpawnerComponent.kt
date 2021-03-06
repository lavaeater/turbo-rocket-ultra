package ecs.components.enemy

import ecs.components.ai.CoolDownComponent

class EnemySpawnerComponent : CoolDownComponent() {
    init {
        coolDownRange = (1f..5f)
        reset()
    }
}