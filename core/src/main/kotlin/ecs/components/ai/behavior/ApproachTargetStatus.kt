package ecs.components.ai.behavior

sealed class ApproachTargetStatus(val name: String) {
    object NotStarted : ApproachTargetStatus("Not started")
    object Approach: ApproachTargetStatus("Move towards player")
}