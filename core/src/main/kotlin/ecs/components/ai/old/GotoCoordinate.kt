package ecs.components.ai.old

import map.grid.Coordinate

class GotoCoordinate: TaskComponent() {
    val coordinate = Coordinate(0, 0)
    override fun toString(): String {
        return "go to: $coordinate"
    }
}