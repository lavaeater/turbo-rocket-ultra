package tru

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ecs.components.graphics.OffsetTextureRegion
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
import ktx.collections.toGdxArray


object SpriteLoader {

    /* Get it to work or ditch it. Right now it ain't even rendering anything.*/

    fun initObjectSprites() : Map<String, Map<SpriteDirection, TextureRegion>> {
        val map = mutableMapOf<String, Map<SpriteDirection, TextureRegion>>()
        for(def in StaticSpriteDefinition.definitions) {
            val texture = Texture(Gdx.files.internal("sprites/${def.sprite}/${def.sprite}.png"))
            map[def.sprite] = def.directions.mapIndexed { row, direction -> direction to
                TextureRegion(
                    texture,
                    row  * def.itemWidth,
                    (def.row + row)  * def.itemHeight,
                    def.itemWidth,
                    def.itemHeight)
            }.toMap()
        }
        return map
    }

    fun initCharachterAnims() : Map<String, Map<AnimState, LpcCharacterAnim<OffsetTextureRegion>>> {
        /*
        For now, we will simply load the sheets and assign anims etc using
        some hardcoded stuff.

        Amazingly, the LPC spritesheets have all anims in all directions.

        So we should have anim and direction as two different things.
         */
        val anims = mutableMapOf<String, MutableMap<AnimState, LpcCharacterAnim<OffsetTextureRegion>>>()
        val characters = listOf("boy", "girl", "enemy")
        for (c in characters) {
            anims[c] = mutableMapOf()
            val texture = Texture(Gdx.files.internal("sprites/$c/$c.png"))
            for (animDef in LpcCharacterAnimDefinition.definitions) {
                anims[c]!![animDef.state] = LpcCharacterAnim<OffsetTextureRegion>(animDef.state,
                    animDef.directions.mapIndexed
                    { row, r -> r to
                    Animation(0.1f, (animDef.frames).map { OffsetTextureRegion(
                            texture,
                        (it)  * animDef.itemWidth,
                        (animDef.row + row)  * animDef.itemHeight,
                            animDef.itemWidth,
                            animDef.itemHeight, 0f, -20f) }.toGdxArray(), animDef.playMode)
                }.toMap())
            }
        }

        val createdFiles = Gdx.files.local("localfiles/created").list("png")
        for(file in createdFiles) {
            val key = file.nameWithoutExtension()
            anims[key] = mutableMapOf()
            val texture = Texture(Gdx.files.local(file.path()))
            for (animDef in LpcCharacterAnimDefinition.definitions) {
                anims[key]!![animDef.state] = LpcCharacterAnim<OffsetTextureRegion>(animDef.state,
                    animDef.directions.mapIndexed
                    { row, r ->
                        r to
                                Animation(0.1f, (animDef.frames).map {
                                    OffsetTextureRegion(
                                        texture,
                                        (it) * animDef.itemWidth,
                                        (animDef.row + row) * animDef.itemHeight,
                                        animDef.itemWidth,
                                        animDef.itemHeight, 0f, -20f
                                    )
                                }.toGdxArray(), animDef.playMode)
                    }.toMap()
                )
            }
        }


//        val json = file.readString()
//        val animDefs = Json.decodeFromString<List<AnimDef>>(json)


//        for (c in newCharacters) {
//            anims[c] = mutableMapOf()
//            val texture = Texture(Gdx.files.internal("sprites/sheets/$c.png"))
//
//            /*
//            mY DATA is a list of animations on states and directions - this must be mapped
//            to a hierarchy
//             */
//
//            for (animDef in animDefs) {
//                val dMap = mutableMapOf<SpriteDirection, Animation<TextureRegion>>()
//                val textureRegions = (animDef.startCol..animDef.endCol).map {
//                        TextureRegion(texture, it * 31, animDef.row * 31, 31,31)
//                    }
//                    dMap[animDef.direction] = Animation(0.2f, textureRegions.toGdxArray(), Animation.PlayMode.LOOP)
//                anims[c]!![animDef.state] = LpcCharacterAnim(animDef.state, dMap)
//            }
//        }
        return anims
    }
}


