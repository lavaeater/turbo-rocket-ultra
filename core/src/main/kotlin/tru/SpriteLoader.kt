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

    fun initObjectSprites(): Map<String, Map<SpriteDirection, TextureRegion>> {
        val map = mutableMapOf<String, Map<SpriteDirection, TextureRegion>>()
        for (def in StaticSpriteDefinition.definitions) {
            val texture = Texture(Gdx.files.internal("sprites/${def.sprite}/${def.sprite}.png"))
            map[def.sprite] = def.directions.mapIndexed { row, direction ->
                direction to
                        TextureRegion(
                            texture,
                            row * def.itemWidth,
                            (def.row + row) * def.itemHeight,
                            def.itemWidth,
                            def.itemHeight
                        )
            }.toMap()
        }
        return map
    }

    fun initEnemyAnims(): Map<String, Map<AnimState, LpcCharacterAnim<OffsetTextureRegion>>> {
        /*
        For now, we will simply load the sheets and assign anims etc using
        some hardcoded stuff.

        Amazingly, the LPC spritesheets have all anims in all directions.

        So we should have anim and direction as two different things.
         */
        val anims = mutableMapOf<String, MutableMap<AnimState, LpcCharacterAnim<OffsetTextureRegion>>>()
        val enemies = listOf("enemy", "zombie")
        for (c in enemies) {
            anims[c] = mutableMapOf()
            val texture = Texture(Gdx.files.internal("sprites/enemies/$c.png"))
            for (animDef in LpcCharacterAnimDefinition.enemyDefinitions) {
                anims[c]!![animDef.state] = LpcCharacterAnim<OffsetTextureRegion>(
                    animDef.state,
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

        return anims
    }

    fun initBossAnims(): Map<String, Map<AnimState, LpcCharacterAnim<OffsetTextureRegion>>> {
        /*
        For now, we will simply load the sheets and assign anims etc using
        some hardcoded stuff.

        Amazingly, the LPC spritesheets have all anims in all directions.

        So we should have anim and direction as two different things.
         */
        val anims = mutableMapOf<String, MutableMap<AnimState, LpcCharacterAnim<OffsetTextureRegion>>>()
        val bosses = listOf("boss_one")
        for (c in bosses) {
            anims[c] = mutableMapOf()
            val texture = Texture(Gdx.files.internal("sprites/bosses/$c.png"))
            for (animDef in LpcCharacterAnimDefinition.enemyDefinitions) {
                anims[c]!![animDef.state] = LpcCharacterAnim<OffsetTextureRegion>(
                    animDef.state,
                    animDef.directions.mapIndexed
                    { row, r ->
                        r to
                                Animation(0.1f, (animDef.frames).map {
                                    OffsetTextureRegion(
                                        texture,
                                        (it) * animDef.itemWidth,
                                        (animDef.row + row) * animDef.itemHeight,
                                        animDef.itemWidth,
                                        animDef.itemHeight, 20f, 0f
                                    )
                                }.toGdxArray(), animDef.playMode)
                    }.toMap()
                )
            }
        }

        return anims
    }

    fun initCharachterAnims(): Map<String, Map<AnimState, LpcCharacterAnim<OffsetTextureRegion>>> {
        /*
        For now, we will simply load the sheets and assign anims etc using
        some hardcoded stuff.

        Amazingly, the LPC spritesheets have all anims in all directions.

        So we should have anim and direction as two different things.
         */
        val anims = mutableMapOf<String, MutableMap<AnimState, LpcCharacterAnim<OffsetTextureRegion>>>()
        val characters = listOf("boy", "girl")
        for (c in characters) {
            anims[c] = mutableMapOf()
            val texture = Texture(Gdx.files.internal("sprites/$c/$c.png"))
            for (animDef in LpcCharacterAnimDefinition.definitions) {
                anims[c]!![animDef.state] = LpcCharacterAnim<OffsetTextureRegion>(
                    animDef.state,
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

        //Just create a different definition for enemy with attack anim specified

        val createdFiles = Gdx.files.local("localfiles/created").list("png")
        for (file in createdFiles) {
            val key = file.nameWithoutExtension()
            anims[key] = mutableMapOf()
            val texture = Texture(Gdx.files.local(file.path()))
            for (animDef in LpcCharacterAnimDefinition.definitions) {
                anims[key]!![animDef.state] = LpcCharacterAnim<OffsetTextureRegion>(
                    animDef.state,
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
        return anims
    }
}


