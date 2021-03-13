package tru

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.collections.toGdxArray

class SpriteManager {

    fun loadSprites() {
        /*
        For now, we will simply load the sheets and assign anims etc using
        some hardcoded stuff.

        Amazingly, the LPC spritesheets have all anims in all directions.

        So we should have anim and direction as two different things.
         */
        val characters = listOf("player", "enemy")
        for (c in characters) {
            val texture = Texture(Gdx.files.internal("sprites/$c/$c.png"))
            for (animDef in LpcCharacterAnimDefinition.definitions) {
                val anim = LpcCharacterAnim(animDef.name,

                    animDef.directions.mapIndexed
                    { row, r -> r to
                    Animation(0.2f, (animDef.frames).map { TextureRegion(
                            texture,
                        (it)  * animDef.itemWidth,
                        (animDef.row + row)  * animDef.itemHeight,
                            animDef.itemWidth,
                            animDef.itemHeight) }.toGdxArray())
                }.toMap())



            }
        }
    }
}



class LpcCharacterAnim(val name: String, val animations: Map<SpriteDirection, Animation<TextureRegion>>)

/**
 * Animations START on one row and have the following three rows included
 * First row is north, second west, third south and fourth is of course east.
 *
 * Nothing stops us from defining the IDLE anim as a one-frame animation, mates
 */
class LpcCharacterAnimDefinition(
    val name: String,
    val row: Int,
    val frames: IntRange,
    val directions: List<SpriteDirection> = SpriteDirection.spriteDirections,
    val itemWidth: Int = 64,
    val itemHeight: Int = 64) {
    companion object {
        val definitions = listOf(
            LpcCharacterAnimDefinition("idle", 8, 0..0),
            LpcCharacterAnimDefinition("walk", 8, 0..8),
            LpcCharacterAnimDefinition("start_aim", 16,0..4),
            LpcCharacterAnimDefinition("aiming", 16,4..4),
            LpcCharacterAnimDefinition("death", 20,0..5, listOf(SpriteDirection.South))
        )
    }
}

sealed class SpriteDirection {
    companion object {
        val spriteDirections = listOf(North, West, South, East)
    }
    object North : SpriteDirection()
    object East:SpriteDirection()
    object South:SpriteDirection()
    object West:SpriteDirection()
}