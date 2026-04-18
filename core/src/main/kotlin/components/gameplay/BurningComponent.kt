package components.gameplay

import com.badlogic.ashley.core.Entity
import components.ai.CoolDownComponent

class BurningComponent : CoolDownComponent() {
    lateinit var player: Entity
    var damageRange = 5..15
    override fun reset() {
        //Burn for this amount of time
        coolDownRange = 3f..8f
        super.reset()
    }

    override fun toString(): String {
        return "Burning"
    }
}