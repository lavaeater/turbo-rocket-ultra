package ecs.components.ai.boss

import ecs.components.ai.TaskComponent

class BossAmble: TaskComponent() {
    init {
        coolDownRange = (3f..8f)
        reset()
    }

    override fun toString(): String {
        return "amble"
    }
}

