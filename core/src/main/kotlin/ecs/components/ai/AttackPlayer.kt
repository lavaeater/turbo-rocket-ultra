package ecs.components.ai

import ktx.math.random

class AttackPlayer: TaskComponent() {
    var coolDown = 0f
    override fun reset() {
        coolDown = 0f
        super.reset()
    }
}