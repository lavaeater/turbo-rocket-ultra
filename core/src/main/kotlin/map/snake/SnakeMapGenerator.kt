package map.snake

class SnakeMapGenerator {
    fun generate() {
        /*

         */
    }
}

sealed class MapDirection {
    object North: MapDirection()
    object East: MapDirection()
    object South: MapDirection()
    object West: MapDirection()
}

class SnakeMapSection(val connections: MutableMap<MapDirection, SnakeMapSection> = mutableMapOf()) {

}