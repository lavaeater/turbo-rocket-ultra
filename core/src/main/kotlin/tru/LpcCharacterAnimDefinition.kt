package tru

import com.badlogic.gdx.graphics.g2d.Animation
import eater.input.CardinalDirection

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
    val directions: List<CardinalDirection> = CardinalDirection.spriteDirections,
    val itemWidth: Int = 64,
    val itemHeight: Int = 64,
    val playMode: Animation.PlayMode = Animation.PlayMode.LOOP
) {
    companion object {
        val definitions = listOf(
            LpcCharacterAnimDefinition(AnimState.Idle, 8, 0..0,),
            LpcCharacterAnimDefinition(AnimState.Walk, 8, 1..8,),
            LpcCharacterAnimDefinition(AnimState.StartAim, 16, 0..4,),
            LpcCharacterAnimDefinition(AnimState.Aiming, 16, 12..12,),
            LpcCharacterAnimDefinition(AnimState.Death, 20, 0..5, listOf(CardinalDirection.South), playMode =  Animation.PlayMode.NORMAL)
        )
        val enemyDefinitions = listOf(
            LpcCharacterAnimDefinition(AnimState.Idle, 8, 0..0,),
            LpcCharacterAnimDefinition(AnimState.Walk, 8, 1..8,),
            LpcCharacterAnimDefinition(AnimState.StartAim, 16, 0..4,),
            LpcCharacterAnimDefinition(AnimState.Slash, 4, 0..7,),
            LpcCharacterAnimDefinition(AnimState.Death, 20, 0..5, listOf(CardinalDirection.South), playMode =  Animation.PlayMode.NORMAL)
        )
    }
}

class StaticSpriteDefinition(
    val sprite: String,
    val row: Int,
    val directions: List<CardinalDirection> = CardinalDirection.spriteDirections,
    val itemWidth: Int = 64,
    val itemHeight: Int = 64
) {
    companion object {
        val definitions = listOf(
            StaticSpriteDefinition("gun", 0)
        )
    }
}