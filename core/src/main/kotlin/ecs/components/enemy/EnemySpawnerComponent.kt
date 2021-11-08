package ecs.components.enemy

import ecs.components.ai.CoolDownComponent

class EnemySpawnerComponent : CoolDownComponent() {
    init {
        coolDownRange = (.25f..1f)
        reset()
    }
}