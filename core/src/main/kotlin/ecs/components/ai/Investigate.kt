package ecs.components.ai

class Investigate: TaskComponent() {
    init {
        coolDownRange = (5f..10f)
        reset()
    }
}