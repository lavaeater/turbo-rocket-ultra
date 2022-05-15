package ecs.components.ai.old

class Panic: TaskComponent() {
    init {
        coolDownRange = (3f..7f)
        reset()
    }

    override fun toString(): String {
        return "panic"
    }
}