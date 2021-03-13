package tru

sealed class SpriteDirection {
    companion object {
        val spriteDirections = listOf(North, West, South, East)
    }
    object North : SpriteDirection()
    object East: SpriteDirection()
    object South: SpriteDirection()
    object West: SpriteDirection()
}