package tru

import audio.TurboSound
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import eater.injection.InjectionContext.Companion.inject
import ecs.systems.graphics.CompassDirection
import features.weapons.GunFrames
import features.weapons.Weapon
import ktx.scene2d.Scene2DSkin
import map.snake.MapDirection
import map.snake.TileAlignment
import space.earlygrey.shapedrawer.ShapeDrawer


/**
 * Actually load assets using the asset manager, maan
 */
object Assets : Disposable {

    lateinit var am: AssetManager

    private val disposables = mutableListOf<Disposable>()

    val speechBTexture by lazy { Texture(Gdx.files.internal("ui/graphics/speechbubble.png")) }
    val speechBubble by lazy { NinePatch(speechBTexture, 14, 8, 12, 12) }

    val tableNinePatch by lazy { Texture(Gdx.files.internal("ui/graphics/convobackground.png")) }
    val tableBackGround by lazy { NinePatch(tableNinePatch, 4, 4, 4, 4) }

    val portrait by lazy { Texture(Gdx.files.internal("portraits/portrait.png")) }

    val characters: Map<String, Map<AnimState, LpcCharacterAnim<TextureRegion>>> by lazy {
        AnimLoader.initCharachterAnims()
    }

    val characterTurboAnims by lazy {
        AnimLoader.initCharacterTurboAnims()
    }

    val enemies by lazy {
        AnimLoader.initEnemyAnims()
    }

    val bosses by lazy {
        AnimLoader.initBossAnims()
    }

    val gunAudio by lazy {
        mapOf(
            "fnp90" to mapOf(
                "shot" to Gdx.audio.newSound(Gdx.files.internal("audio/fnp90-shot.ogg")),
                "reload" to Gdx.audio.newSound(Gdx.files.internal("audio/fnp90-reload.ogg"))
            ),
            "spas12" to mapOf(
                "shot" to Gdx.audio.newSound(Gdx.files.internal("audio/spas12-shot.ogg")),
                "reload" to Gdx.audio.newSound(Gdx.files.internal("audio/spas12-reload.ogg"))
            ),
            "glock17" to mapOf(
                "shot" to Gdx.audio.newSound(Gdx.files.internal("audio/glock17-shot.ogg")),
                "reload" to Gdx.audio.newSound(Gdx.files.internal("audio/glock17-reload.ogg"))
            )
        )
    }

    val newSoundEffects: Map<String, Map<String, List<TurboSound>>> by lazy {
        mapOf(
            "zombies" to mapOf(
                "groans" to listOf(
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/zombies/zombie-groan-1.wav")),
                        3026f
                    ),
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/zombies/zombie-groan-2.wav")),
                        866f
                    )
                ),
                "panic" to listOf(
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/zombies/panic-1.wav")),
                        1256f
                    ),
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/zombies/panic-2.wav")),
                        10703f
                    )
                ),
                "attacks" to listOf(
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/zombies/zombie-attack.wav")),
                        8010f
                    )
                )
            ),
            "weapons" to mapOf(
                "molotov" to listOf(
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/molotov/molotov-1.wav")),
                        5885f
                    )
                ),
                "grenade" to listOf(
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/grenade/grenade-1.wav")),
                        1176f
                    )
                )
            ),
            "misc" to mapOf(
                "flesh" to listOf(
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/misc/flesh-1.wav")),
                        1323f
                    ),
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/misc/flesh-2.wav")),
                        280f
                    )
                )
            ),
            "players" to mapOf(
                "out-of-ammo" to listOf(
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/players/out-of-ammo-1.wav")),
                        835f
                    ),
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/players/out-of-ammo-2.wav")),
                        1915f
                    )
                ),
                "death" to listOf(
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/players/death-1.wav")),
                        1691f
                    ),
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/players/death-2.wav")),
                        2450f
                    )
                ),
                "loot-found" to listOf(
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/players/loot-1.wav")),
                        2530f
                    ),
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/players/loot-2.wav")),
                        1288f
                    ),
                    TurboSound(
                        Gdx.audio.newSound(Gdx.files.internal("audio/players/loot-3.wav")),
                        1603f
                    ),
                    TurboSound(
                        Gdx.audio.newSound(
                            Gdx.files.internal(
                                "audio/players/loot-4.wav"
                            )
                        ),
                        1706f
                    )
                ),
                "one-liners" to listOf(
                    TurboSound(
                        Gdx.audio.newSound(
                            Gdx.files.internal(
                                "audio/players/groovy.wav"
                            )
                        ),
                        824f
                    ),
                    TurboSound(
                        Gdx.audio.newSound(
                            Gdx.files.internal(
                                "audio/players/lets-dance.wav"
                            )
                        ),
                        963f
                    ),
                    TurboSound(
                        Gdx.audio.newSound(
                            Gdx.files.internal(
                                "audio/players/there-u-go.wav"
                            )
                        ),
                        661f
                    )
                )
            )
        )
    }

    val music by lazy {
        listOf(Gdx.audio.newMusic(Gdx.files.internal("audio/music/track-1.mp3")))
    }

    val enemyGibs by lazy {
        val texture = Texture(Gdx.files.internal("sprites/enemies/enemy_gibs.png"))
        listOf(
            TextureRegion(texture, 0, 0, 24, 24),
            TextureRegion(texture, 24, 0, 24, 24),
            TextureRegion(texture, 48, 0, 24, 24),
            TextureRegion(texture, 72, 0, 24, 24),
            TextureRegion(texture, 96, 0, 24, 24),
            TextureRegion(texture, 0, 24, 24, 24),
            TextureRegion(texture, 24, 24, 24, 24),
            TextureRegion(texture, 48, 24, 24, 24),
            TextureRegion(texture, 72, 24, 24, 24),
            TextureRegion(texture, 96, 24, 24, 24)
        )
    }

    private val handgunSprite by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/weapons/pistol.png")))
    }

    private val handgunTopSprite by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/weapons/pistol-top.png")))
    }
    private val handgunBottomSprite by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/weapons/pistol-bottom.png")))
    }

    private val spas12Sprite by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/weapons/shotgun.png")))
    }

    private val spas12Top by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/weapons/shotgun-top.png")))
    }
    private val spas12Bottom by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/weapons/shotgun-bottom.png")))
    }

    private val batSprite by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/weapons/bat.png")), 0, 0, 32, 5)
    }

    val weapons: Map<String, Map<CardinalDirection, TextureRegion>> by lazy {
        mapOf(
            GunFrames.handGun to
                    mapOf(
                        CardinalDirection.North to
                                handgunTopSprite,
                        CardinalDirection.West to
                                handgunSprite,
                        CardinalDirection.South to
                                handgunBottomSprite,
                        CardinalDirection.East to
                                handgunSprite
                    ),
            GunFrames.spas12 to
                    mapOf(
                        CardinalDirection.North to
                                spas12Top,
                        CardinalDirection.West to
                                spas12Sprite,
                        CardinalDirection.South to
                                spas12Bottom,
                        CardinalDirection.East to
                                spas12Sprite
                    ),
            GunFrames.bat to
                    mapOf(
                        CardinalDirection.North to
                                batSprite,
                        CardinalDirection.West to
                                batSprite,
                        CardinalDirection.South to
                                batSprite,
                        CardinalDirection.East to
                                batSprite,
                    )
        )
    }

    val isoTowers by lazy {
        val t = Texture(Gdx.files.internal("iso/towers.png"))
        mapOf("obstacle" to TextureRegion(t, 0,0,32,32).apply { flip(false, true) },
            "objective" to TextureRegion(t, 32,0,32,32).apply { flip(false, true) })
    }

    val towers by lazy {
        mapOf(
            "machinegun" to TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-1.png"))),
            "flamethrower" to TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-1.png"))),
            "obstacle" to TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-obstacle.png"))),
            "objective" to TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-objective.png"))),
            "noise" to TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-1.png")))
        )
    }

    val newTower by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-template.png")))
    }

    val lootBox by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/loot/lootbox.png"))).apply { flip(false, true) }
    }
    val arrowTexture by lazy {
        Texture(Gdx.files.internal("sprites/arrows.png"))
    }

    val splatterEffectPool: ParticleEffectPool by lazy {
        val pe = ParticleEffect()
        pe.load(
            Gdx.files.internal("particles/blood_splatter/blood_splatter.effect"),
            Gdx.files.internal("particles/blood_splatter")
        )
        pe.scaleEffect(0.025f)
        ParticleEffectPool(pe, 1000, 3000)
    }

    val fireEffectPool: ParticleEffectPool by lazy {
        val pe = ParticleEffect()
        pe.load(Gdx.files.internal("particles/flame/pp_flame.p"), Gdx.files.internal("particles/flame"))
        pe.scaleEffect(0.025f)
        ParticleEffectPool(pe, 1000, 3000)
    }

    val explosionEffectPool: ParticleEffectPool by lazy {
        val pe = ParticleEffect()
        pe.load(Gdx.files.internal("particles/explosion/explosion.p"), Gdx.files.internal("particles/explosion"))
        pe.scaleEffect(0.025f)
        ParticleEffectPool(pe, 10, 100)
    }

    val arrows by lazy {
        mapOf(
            MapDirection.North to TextureRegion(arrowTexture, 0, 0, 16, 16),
            MapDirection.West to TextureRegion(arrowTexture, 16, 0, 16, 16),
            MapDirection.South to TextureRegion(arrowTexture, 16, 16, 16, 16),
            MapDirection.East to TextureRegion(arrowTexture, 0, 16, 16, 16)
        )
    }

    val isoFloorTiles by lazy {
        val t = Texture(Gdx.files.internal("iso/floor_new.png"))
        Array(2) { i ->
            val x = i * 32
            val y = 0
            val w = 32
            val h = 32
            TextureRegion(t, x, y, w, h).apply {
                this.flip(false, true)
            }
        }.toList()
    }

    val isoWallTiles by lazy {
        val t = Texture(Gdx.files.internal("iso/wall_new.png"))
        Array(2) { i ->
            val x = i * 32
            val y = 0
            val w = 32
            val h = 32
            TextureRegion(t, x, y, w, h).apply {
                this.flip(false, true)
            }
        }.toList()
    }

    val tileTexture by lazy {
        Texture(Gdx.files.internal("tiles/tiles.png"))
    }
    val tiles by lazy {
        val ts = mutableMapOf<String, TextureRegion>()
        for (xTile in 0..3)
            for (yTile in 0..2) {
                when (yTile) {
                    0 -> ts["floor$xTile"] = TextureRegion(tileTexture, xTile * 16, yTile * 16, 16, 16)
                    1 -> {
                        when (xTile) {
                            0 -> ts["wall$xTile"] = TextureRegion(tileTexture, xTile * 16, yTile * 16, 16, 16)
                            1 -> ts["wall$xTile"] = TextureRegion(tileTexture, xTile * 16, yTile * 16, 16, 16)
                            2 -> {} //ts["wall_end"] = TextureRegion(tileTexture, xTile * 16, yTile * 16, 16, 16)
                        }
                    }

                    2 -> {
                        if (xTile in 0 until 1)
                            ts["wall_end"] = TextureRegion(tileTexture, xTile * 16, yTile * 16, 16, 16)
                        if (xTile == 2)
                            ts["build_crate"] = TextureRegion(tileTexture, xTile * 16, yTile * 16, 16, 16)
                    }
                }
            }
        ts
    }

    val floorTiles by lazy {
        tiles.filterKeys { it.contains("floor") }.values.toList()
    }

    val wallTiles by lazy {
        tiles.filterKeys { it.contains("wall") && !it.contains("_end") && !it.contains("_shadow") }.values.toList()
    }
    val wallEndTile by lazy {
        tiles.filterKeys { it == "wall_end" }.values.first()
    }
    val buildables by lazy {
        tiles.filterKeys { it.contains("build") }.values.toList()
    }

    val aiDebugBadges by lazy {
        val texture = Texture(Gdx.files.internal("sprites/bt_labels/bt_labels.png"))
        mapOf(
            "amble" to TextureRegion(texture, 0, 0, 64, 12),
            "attack" to TextureRegion(texture, 0, 12, 64, 12),
            "chase" to TextureRegion(texture, 0, 24, 64, 12),
            "check" to TextureRegion(texture, 0, 36, 64, 12),
            "seek" to TextureRegion(texture, 0, 48, 64, 12),
            "grabthrow" to TextureRegion(texture, 0, 60, 64, 12),
            "rush" to TextureRegion(texture, 0, 72, 64, 12),
            "panic" to TextureRegion(texture, 0, 84, 64, 12)
        )
    }

    val ps4Buttons by lazy {
        val texture = Texture(Gdx.files.internal("controllers/PS4.png"))
        mapOf(
            "cross" to TextureRegion(texture, 32, 48, 16, 16),
            "square" to TextureRegion(texture, 32, 64, 16, 16),
            "triangle" to TextureRegion(texture, 32, 80, 16, 16),
            "circle" to TextureRegion(texture, 32, 96, 16, 16),
            "dpadup" to TextureRegion(texture, 0, 16, 16, 16),
            "dpadright" to TextureRegion(texture, 0, 32, 16, 16),
            "dpaddown" to TextureRegion(texture, 0, 48, 16, 16),
            "dpadleft" to TextureRegion(texture, 0, 64, 16, 16)
        )
    }
    val bullet by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/bullets/bullet.png")))
    }

    val molotov by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/weapons/molotov.png")))
    }

    val playerCharacters by lazy { characters }

    val splashTexture: Texture by lazy {
        Texture(Gdx.files.internal("graphics/splash-screen.png"))
    }

    val dummyRegion: TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()
        TextureRegion(texture, 0, 0, 1, 1)
    }

    val shapeDrawerRegion: TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()
        TextureRegion(texture, 0, 0, 1, 1)
    }

    val shapeDrawer: ShapeDrawer by lazy {
        ShapeDrawer(inject<PolygonSpriteBatch>() as Batch, shapeDrawerRegion)
    }

//    val objectSprites by lazy { SpriteLoader.initObjectSprites() }

    val soundEffects: Map<String, Sound> by lazy {
        mapOf(
            "gunshot" to Gdx.audio.newSound(Gdx.files.internal("audio/gunshot.wav")),
            "shellcasing" to Gdx.audio.newSound(Gdx.files.internal("audio/shellcasing.wav"))
        )
    }

    val font by lazy {
        BitmapFont(Gdx.files.internal("font/arial-15.fnt"))
    }

    val debugFont: BitmapFont by lazy {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("font/a-goblin-appears.ttf"))
        val parameter = FreeTypeFontParameter()
        parameter.size = 12
        parameter.magFilter = Texture.TextureFilter.Linear
        parameter.minFilter = Texture.TextureFilter.Linear
        parameter.flip = true
        val font32 = generator.generateFont(parameter) // font size 32 pixels

        font32.data.setScale(0.1f)
        generator.dispose()
        font32
    }

    fun load(): AssetManager {
        am = AssetManager()
        fixScene2dSkin()
        fixFlip()
        return am
    }

    private fun fixFlip() {
        for (t in towers.values)
            t.flip(true, true)

        newTower.flip(true, true)

        for (t in aiDebugBadges.values)
            t.flip(true, false)

        for (c in playerCharacters.values)
            for (a in c.values)
                for (b in a.animations.values)
                    for (d in b.keyFrames) {
                        d.flip(true, true)

                    }
        for (c in enemies.values)
            for (a in c.values)
                for (b in a.animations.values)
                    for (d in b.keyFrames) {
                        d.flip(true, true)
                    }
        for (c in bosses.values) {
            for (a in c.values)
                for (b in a.animations.values)
                    for (d in b.keyFrames) {
                        d.flip(true, true)
                    }
        }
    }

    val foregroundColor = Color(
        MathUtils.norm(0f, 255f, 255f),
        MathUtils.norm(0f, 255f, 111f),
        0f,
        1f
    )

    val backgroundColor = Color(
        MathUtils.norm(0f, 255f, 11f),
        MathUtils.norm(0f, 255f, 12f),
        MathUtils.norm(0f, 255f, 57f),
        1f
    )

    private fun fixScene2dSkin() {
        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("skins/my-skin/uiskin.json"))
    }

    override fun dispose() {
    }
}

fun Map<String, Map<String, List<TurboSound>>>.getRandomSoundFor(category: String, subCategory: String): TurboSound {
    return this[category]!![subCategory]!!.random()
}

fun Map<String, Map<AnimState, LpcCharacterAnim<TextureRegion>>>.getFirstFor(
    anim: AnimState,
    direction: CardinalDirection
): Animation<TextureRegion> {
    return this.values.first()[anim]!!.animations[direction]!!
}

fun Map<String, Map<AnimState, LpcCharacterAnim<TextureRegion>>>.getAnimationFor(
    character: String,
    anim: AnimState,
    direction: CardinalDirection
): Animation<TextureRegion> {
    return this[character]!![anim]!!.animations[direction]!!
}

fun Map<String, Map<CardinalDirection, TextureRegion>>.getSpriteFor(
    weapon: Weapon,
    direction: CardinalDirection
): TextureRegion {
    return this[weapon.textureName]!![direction]!!
}

