package tru

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.collections.toGdxArray

data class TurboCharacterAnim(val key: String, val name: String, val animations: Map<AnimState, LpcCharacterAnim<TextureRegion>>) {
    fun animationFor(animState: AnimState, direction: CardinalDirection) : Animation<TextureRegion> {
        return animations[animState]!!.animations[direction]!!
    }
}

object AnimLoader {

    /* Get it to work or ditch it. Right now it ain't even rendering anything.*/

    fun initObjectSprites(): Map<String, Map<CardinalDirection, TextureRegion>> {
        val map = mutableMapOf<String, Map<CardinalDirection, TextureRegion>>()
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

    fun initEnemyAnims(): Map<String, Map<AnimState, LpcCharacterAnim<TextureRegion>>> {
        /*
        For now, we will simply load the sheets and assign anims etc using
        some hardcoded stuff.

        Amazingly, the LPC spritesheets have all anims in all directions.

        So we should have anim and direction as two different things.
         */
        val anims = mutableMapOf<String, MutableMap<AnimState, LpcCharacterAnim<TextureRegion>>>()
        val enemies = listOf("enemy", "zombie")
        for (c in enemies) {
            anims[c] = mutableMapOf()
            val texture = Texture(Gdx.files.internal("sprites/enemies/$c.png"))
            for (animDef in LpcCharacterAnimDefinition.enemyDefinitions) {
                anims[c]!![animDef.state] = LpcCharacterAnim<TextureRegion>(
                    animDef.state,
                    animDef.directions.mapIndexed
                    { row, r ->
                        r to
                                Animation(0.1f, (animDef.frames).map {
                                    TextureRegion(
                                        texture,
                                        (it) * animDef.itemWidth,
                                        (animDef.row + row) * animDef.itemHeight,
                                        animDef.itemWidth,
                                        animDef.itemHeight
                                    )
                                }.toGdxArray(), animDef.playMode)
                    }.toMap()
                )
            }
        }

        return anims
    }

    fun initBossAnims(): Map<String, Map<AnimState, LpcCharacterAnim<TextureRegion>>> {
        /*
        For now, we will simply load the sheets and assign anims etc using
        some hardcoded stuff.

        Amazingly, the LPC spritesheets have all anims in all directions.

        So we should have anim and direction as two different things.
         */
        val anims = mutableMapOf<String, MutableMap<AnimState, LpcCharacterAnim<TextureRegion>>>()
        val bosses = listOf("boss_one")
        for (c in bosses) {
            anims[c] = mutableMapOf()
            val texture = Texture(Gdx.files.internal("sprites/bosses/$c.png"))
            for (animDef in LpcCharacterAnimDefinition.enemyDefinitions) {
                anims[c]!![animDef.state] = LpcCharacterAnim<TextureRegion>(
                    animDef.state,
                    animDef.directions.mapIndexed
                    { row, r ->
                        r to
                                Animation(0.1f, (animDef.frames).map {
                                    TextureRegion(
                                        texture,
                                        (it) * animDef.itemWidth,
                                        (animDef.row + row) * animDef.itemHeight,
                                        animDef.itemWidth,
                                        animDef.itemHeight
                                    )
                                }.toGdxArray(), animDef.playMode)
                    }.toMap()
                )
            }
        }

        return anims
    }

    fun initCharacterTurboAnims(): List<TurboCharacterAnim> {
        val animList = mutableListOf<TurboCharacterAnim>()
        val characterNames = mapOf("Genku" to "boy", "Zdanya" to "girl", "Atomic" to "blondie", "Grafen" to "curly", "Hangko" to "green", "Hogan" to "hogan", "KNIGHT" to "knight", "Red Sonja" to "redsonja", "Skeltor" to "skeleton")
        for((n, k) in characterNames) {
            val someAnims = mutableMapOf<AnimState, LpcCharacterAnim<TextureRegion>>()
            val texture = Texture(Gdx.files.internal("sprites/lpc/$k.png"))
            for (animDef in LpcCharacterAnimDefinition.definitions) {
                someAnims[animDef.state] = LpcCharacterAnim<TextureRegion>(
                    animDef.state,
                    animDef.directions.mapIndexed
                    { row, r ->
                        r to
                                Animation(0.1f, (animDef.frames).map {
                                    TextureRegion(
                                        texture,
                                        (it) * animDef.itemWidth,
                                        (animDef.row + row) * animDef.itemHeight,
                                        animDef.itemWidth,
                                        animDef.itemHeight)
                                }.toGdxArray(), animDef.playMode)
                    }.toMap()
                )
            }
            animList.add(TurboCharacterAnim(k, n, someAnims))
        }
        return animList
    }

    fun initCharachterAnims(): Map<String, Map<AnimState, LpcCharacterAnim<TextureRegion>>> {
        /*
        For now, we will simply load the sheets and assign anims etc using
        some hardcoded stuff.

        Amazingly, the LPC spritesheets have all anims in all directions.

        So we should have anim and direction as two different things.
         */
        val anims = mutableMapOf<String, MutableMap<AnimState, LpcCharacterAnim<TextureRegion>>>()
        val characters = listOf("boy", "girl","blondie", "curly", "green", "hogan", "knight", "redsonja", "skeleton")

        for (c in characters) {
            anims[c] = mutableMapOf()
            val texture = Texture(Gdx.files.internal("sprites/lpc/$c.png"))
            for (animDef in LpcCharacterAnimDefinition.definitions) {
                anims[c]!![animDef.state] = LpcCharacterAnim<TextureRegion>(
                    animDef.state,
                    animDef.directions.mapIndexed
                    { row, r ->
                        r to
                                Animation(0.1f, (animDef.frames).map {
                                    TextureRegion(
                                        texture,
                                        (it) * animDef.itemWidth,
                                        (animDef.row + row) * animDef.itemHeight,
                                        animDef.itemWidth,
                                        animDef.itemHeight)
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
                anims[key]!![animDef.state] = LpcCharacterAnim<TextureRegion>(
                    animDef.state,
                    animDef.directions.mapIndexed
                    { row, r ->
                        r to
                                Animation(0.1f, (animDef.frames).map {
                                    TextureRegion(
                                        texture,
                                        (it) * animDef.itemWidth,
                                        (animDef.row + row) * animDef.itemHeight,
                                        animDef.itemWidth,
                                        animDef.itemHeight
                                    )
                                }.toGdxArray(), animDef.playMode)
                    }.toMap()
                )
            }
        }
        return anims
    }
}


