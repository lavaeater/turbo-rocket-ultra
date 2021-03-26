package ecs.components.ai

import ktx.math.random

class Amble: TaskComponent() {
    var coolDown = (5f..10f).random()
    override fun reset() {
        coolDown = (5f..10f).random()
        super.reset()
    }
}

