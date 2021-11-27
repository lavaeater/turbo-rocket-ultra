package ecs.components.ai

class Investigate: TaskComponent() {
    var needsNew = true
    override fun reset() {
        needsNew = true
        coolDownRange = (5f..10f)
        super.reset()
    }

    override fun toString(): String {
        return "check"
    }

}