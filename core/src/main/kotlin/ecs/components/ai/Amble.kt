package ecs.components.ai

import com.badlogic.ashley.core.Component
import ktx.math.vec2

class Amble: TaskComponent() {
    var coolDown = 30f
    override fun reset() {
        coolDown = 30f
        super.reset()
    }
}