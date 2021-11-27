package ecs.components.ai

class Amble: TaskComponent() {
    var needsNew = true

    override fun reset() {
        coolDownRange = (30f..180f)
        needsNew = true
        super.reset()
    }

    override fun toString(): String {
        return "amble"
    }
}

