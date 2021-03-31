package ecs.components.ai

class Investigate: TaskComponent() {
    init {
        coolDownRange = (30f..60f)
        reset()
    }
}