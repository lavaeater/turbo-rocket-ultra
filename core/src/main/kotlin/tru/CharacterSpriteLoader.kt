package tru

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.collections.toGdxArray

object CharacterSpriteLoader {

    fun initCharachterAnims() : Map<String, Map<String, LpcCharacterAnim>> {
        /*
        For now, we will simply load the sheets and assign anims etc using
        some hardcoded stuff.

        Amazingly, the LPC spritesheets have all anims in all directions.

        So we should have anim and direction as two different things.
         */
        val anims = mutableMapOf<String, MutableMap<String, LpcCharacterAnim>>()
        val characters = listOf("player", "enemy")
        for (c in characters) {
            anims[c] = mutableMapOf()
            val texture = Texture(Gdx.files.internal("sprites/$c/$c.png"))
            for (animDef in LpcCharacterAnimDefinition.definitions) {
                anims[c]!![animDef.name] = LpcCharacterAnim(animDef.name,
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
        return anims
    }
}


