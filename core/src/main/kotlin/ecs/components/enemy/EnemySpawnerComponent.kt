package ecs.components.enemy

import ecs.components.ai.CoolDownComponent

class EnemySpawnerComponent : CoolDownComponent() {
    var waveSize = 1
    init {
        coolDownRange = (10f..30f)
        reset()
    }
}