package ecs.components.ai.old

class Amble: TaskComponent() {
    var needsNew = true

    override fun reset() {
        coolDownRange = (10f..30f)
        needsNew = true
        super.reset()
    }

    override fun toString(): String {
        return "amble"
    }
}

