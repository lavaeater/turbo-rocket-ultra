package components.ai.boss

import components.ai.TaskComponent
import ktx.math.vec2

class RushPlayer : TaskComponent() {
    val rushPoint = vec2()
    var previousDistance = 0f

    override fun reset() {
        super.reset()
        previousDistance = 0f
        rushPoint.setZero()
    }
    override fun toString(): String {
        return "rush player"
    }
}