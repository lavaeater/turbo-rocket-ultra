package components

sealed class ApproachTargetStatus(val name: String) {
    object NotStarted : ApproachTargetStatus("Not started")
    object Approach: ApproachTargetStatus("Move towards player")
}