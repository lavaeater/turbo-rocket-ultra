package ecs.components.ai.behavior

sealed class AmbleStatus(val name: String) {
    object NotStarted : AmbleStatus("Not started")
    object FindingTargetCoordinate : AmbleStatus("Looking For Endpoint")
    object FindingPathToTarget : AmbleStatus("Finding Path")
    object NeedsWaypoint : AmbleStatus("Needs Waypoint")
    object MoveToWaypoint : AmbleStatus("Moving to Waypoint")
}