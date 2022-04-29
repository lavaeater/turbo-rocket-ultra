package tru

import kotlinx.serialization.Serializable

@Serializable
sealed class SpriteDirection {
    companion object {
        val spriteDirections = listOf(North, West, South, East)
    }
    override fun toString(): String {
        return this::class.toString().substringAfter(".").substringAfter("$").substringBefore("@")
    }
    @Serializable
    object North : SpriteDirection()
    @Serializable
    object East: SpriteDirection()
    @Serializable
    object South: SpriteDirection()
    @Serializable
    object West: SpriteDirection()
}

