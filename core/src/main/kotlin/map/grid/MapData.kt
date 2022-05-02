package map.grid

data class MapData(
    val name: String,
    val startMessage: String,
    val successMessage: String,
    val failMessage: String,
    val mapFile: String,
    val maxEnemies: Int = 240,
    val maxSpawnedEnemies: Int = 120,
    val mapDefinition: TextGridMapDefinition
)