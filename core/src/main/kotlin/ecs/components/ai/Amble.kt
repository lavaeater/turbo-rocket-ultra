package ecs.components.ai

class Amble: TaskComponent() {
    init {
        coolDownRange = (15f..90f)
        reset()
    }
}

