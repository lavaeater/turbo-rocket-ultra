package ecs.components.enemy

import ecs.components.ai.CoolDownComponent

class EnemySpawnerComponent : CoolDownComponent() {
    var waveSize = 1
    init {
        coolDownRange = (1f..30f)
        reset()
    }
}