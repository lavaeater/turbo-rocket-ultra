package ecs.components.ai

class Amble: TaskComponent() {
    init {
        coolDownRange = (5f..15f)
        reset()
    }

    override fun toString(): String {
        return "amble"
    }
}

