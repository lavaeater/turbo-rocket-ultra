package tru

//import kotlinx.serialization.Serializable

//@Serializable
sealed class CardinalDirection {
    companion object {
        val spriteDirections = listOf(North, West, South, East)
    }
    override fun toString(): String {
        return this::class.toString().substringAfter(".").substringAfter("$").substringBefore("@")
    }
    object North : CardinalDirection()
    object East: CardinalDirection()
    object South: CardinalDirection()
    object West: CardinalDirection()
}

