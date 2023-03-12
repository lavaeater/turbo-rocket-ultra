package eater.ecs.ashley.components

sealed class PointType(val character: String) {
    object BlobStart: PointType("2")
    object PlayerStart: PointType("3")
    object HumanStart: PointType("4")
    object Lights: PointType("5")
    object Impassable: PointType("1")

    companion object {
        val allTypes = listOf(BlobStart, PlayerStart, HumanStart, Lights, Impassable).associateBy { it.character }
    }
}