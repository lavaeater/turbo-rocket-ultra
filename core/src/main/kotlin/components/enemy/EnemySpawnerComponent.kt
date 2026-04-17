package components.enemy

import components.ai.CoolDownComponent

class EnemySpawnerComponent : CoolDownComponent() {
    var waveSize = 1
    init {
        coolDownRange = (1f..30f)
        reset()
    }
}