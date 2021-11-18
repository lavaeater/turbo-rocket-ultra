package ecs.systems.graphics

import map.grid.Coordinate

sealed class CompassDirection {
    object North : CompassDirection()
    object NorthEast : CompassDirection()
    object East : CompassDirection()
    object SouthEast : CompassDirection()
    object South : CompassDirection()
    object SouthWest : CompassDirection()
    object West : CompassDirection()
    object NorthWest : CompassDirection()
    companion object {
        val directions = listOf(North, NorthEast, East, SouthEast, South, SouthWest, West, NorthWest)
        val directionOffsets = mapOf(
            North to Coordinate(0, -1),
            NorthEast to Coordinate(-1, -1),
            East to Coordinate(-1, 0),
            SouthEast to Coordinate(-1, 1),
            South to Coordinate(0, 1),
            SouthWest to Coordinate(1, 1),
            West to Coordinate(1, 0),
            NorthWest to Coordinate(1, -1)
        )
    }
}