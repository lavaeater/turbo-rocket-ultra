package components.gameplay

import components.ai.CoolDownComponent
import data.Player

class GrenadeComponent: CoolDownComponent() {
    lateinit var player: Player
    override fun reset() {
        coolDownRange = 3f..5f
        super.reset()
    }
}