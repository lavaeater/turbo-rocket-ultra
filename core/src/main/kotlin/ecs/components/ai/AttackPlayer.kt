package ecs.components.ai

class AttackPlayer: TaskComponent() {
    init {
        coolDownRange = (.1f..0.5f)
        coolDown = 0f
    }
}