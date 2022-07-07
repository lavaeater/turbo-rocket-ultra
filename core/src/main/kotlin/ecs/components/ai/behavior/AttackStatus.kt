package ecs.components.ai.behavior

sealed class AttackStatus(val name: String) {
    object NotStarted : AttackStatus("Not started")
    object Attacking: AttackStatus("Move towards player")
}