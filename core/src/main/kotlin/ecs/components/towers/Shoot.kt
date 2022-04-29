package ecs.components.towers

import ecs.components.ai.TaskComponent

class Shoot : TaskComponent() {
    init {
        coolDownRange = (0.1f..0.1f)
        reset()
    }
}