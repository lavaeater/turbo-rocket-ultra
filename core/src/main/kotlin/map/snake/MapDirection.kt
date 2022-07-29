package map.snake

sealed class MapDirection {
    object North : MapDirection()
    object East : MapDirection()
    object South : MapDirection()
    object West : MapDirection()
    companion object {
        val opposing by lazy { mapOf(North to South, South to North, East to West, West to East) }
        val directions: List<MapDirection> by lazy { listOf(North, East, South, West) }
        val xIndex by lazy { mapOf(North to 0, South to 0, East to 1, West to -1) }
        val yIndex by lazy { mapOf(North to -1, South to 1, East to 0, West to 0) }
        val directionDegrees by lazy { mapOf(North to 90f, East to 180f, South to 270f, West to 0f) }
    }
}