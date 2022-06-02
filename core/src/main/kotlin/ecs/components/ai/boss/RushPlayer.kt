package ecs.components.ai.boss

import ecs.components.ai.TaskComponent
import ktx.math.vec2

/**
 * Either we can or we can't do it like this. Would be cool
 * if we could, but perhaps components are indeed their lowest class?
 */
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