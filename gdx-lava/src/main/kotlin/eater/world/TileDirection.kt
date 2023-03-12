package eater.world

sealed class TileDirection(val x: Int, val y: Int) {
    object West : TileDirection(-1, 0)
    object NorthWest : TileDirection(-1, -1)
    object North : TileDirection(0, -1)
    object NorthEast : TileDirection(1, -1)
    object East : TileDirection(1, 0)
    object SouthEast : TileDirection(1, 1)
    object South : TileDirection(0, 1)
    object SouthWest : TileDirection(-1, 1)

    companion object {
        val directions = listOf(West, NorthWest, North, NorthEast, East, SouthEast, South, SouthWest)
    }
}