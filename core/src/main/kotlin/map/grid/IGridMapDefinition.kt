package map.grid

interface IGridMapDefinition {
    val booleanSections: Array<Array<Boolean>>
    fun hasLoot(coordinate: Coordinate): Boolean
    fun hasStart(coordinate: Coordinate): Boolean
    fun hasGoal(coordinate: Coordinate): Boolean
    fun hasSpawner(coordinate: Coordinate): Boolean
    fun hasBoss(coordinate: Coordinate): Boolean
    fun hasHackingStation(coordinate: Coordinate) : Boolean
    fun hasTarget(coordinate: Coordinate): Boolean
}