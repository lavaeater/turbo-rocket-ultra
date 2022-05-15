package ecs.components.ai.old

class AttackPlayer: TaskComponent() {
    init {
        coolDownRange = (.1f..0.5f)
        coolDown = 0f
    }
    override fun toString(): String {
        return "attack"
    }

}