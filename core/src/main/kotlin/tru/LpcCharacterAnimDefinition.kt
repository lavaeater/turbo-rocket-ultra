package tru

/**
 * Animations START on one row and have the following three rows included
 * First row is north, second west, third south and fourth is of course east.
 *
 * Nothing stops us from defining the IDLE anim as a one-frame animation, mates
 */
class LpcCharacterAnimDefinition(
    val state: AnimState,
    val row: Int,
    var frames: IntRange,
    val directions: List<SpriteDirection> = SpriteDirection.spriteDirections,
    val itemWidth: Int = 64,
    val itemHeight: Int = 64) {
    companion object {
        val definitions = listOf(
            LpcCharacterAnimDefinition(AnimState.Idle, 8, 0..0),
            LpcCharacterAnimDefinition(AnimState.Walk, 8, 0..8),
            LpcCharacterAnimDefinition(AnimState.StartAim, 16, 0..4),
            LpcCharacterAnimDefinition(AnimState.Aiming, 16, 11..11),
            LpcCharacterAnimDefinition(AnimState.Death, 20, 0..5, listOf(SpriteDirection.South))
        )
    }
}

class StaticSpriteDefinition(
    val sprite: String,
    val row: Int,
    val directions: List<SpriteDirection> = SpriteDirection.spriteDirections,
    val itemWidth: Int = 64,
    val itemHeight: Int = 64
) {
    companion object {
        val definitions = listOf(
            StaticSpriteDefinition("gun", 0)
        )
    }
}