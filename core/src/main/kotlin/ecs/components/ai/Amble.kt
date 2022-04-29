package ecs.components.ai

class Amble: TaskComponent() {
    init {
        coolDownRange = (3f..7f)
        reset()
    }

    override fun toString(): String {
        return "amble"
    }
}

