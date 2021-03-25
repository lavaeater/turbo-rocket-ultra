package ecs.components.ai

import ktx.math.random

class Amble: TaskComponent() {
    var coolDown = (20f..45f).random()
    override fun reset() {
        coolDown = (20f..45f).random()
        super.reset()
    }
}