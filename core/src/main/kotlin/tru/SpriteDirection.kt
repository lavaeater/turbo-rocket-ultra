package tru

//import kotlinx.serialization.Serializable

//@Serializable
sealed class SpriteDirection {
    companion object {
        val spriteDirections = listOf(North, West, South, East)
    }
    override fun toString(): String {
        return this::class.toString().substringAfter(".").substringAfter("$").substringBefore("@")
    }
    object North : SpriteDirection()
    object East: SpriteDirection()
    object South: SpriteDirection()
    object West: SpriteDirection()
}

