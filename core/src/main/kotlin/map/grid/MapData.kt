package map.grid

data class MapData(
    val name: String,
    val startMessage: String,
    val successMessage: String,
    val failMessage: String,
    val mapFile: String,
    val facts: Map<String, Any>,
    val storyKeys: List<String>,
    val mapDefinition: TextGridMapDefinition
)