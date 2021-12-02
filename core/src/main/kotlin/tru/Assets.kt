package tru

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import features.weapons.GunFrames
import injection.Context.inject
import ktx.scene2d.Scene2DSkin
import map.snake.MapDirection
import space.earlygrey.shapedrawer.ShapeDrawer


/**
 * Actually load assets using the asset manager, maan
 */
object Assets : Disposable {

    lateinit var am: AssetManager


    val speechBTexture by lazy { Texture(Gdx.files.internal("ui/graphics/speechbubble.png")) }
    val speechBubble by lazy { NinePatch(speechBTexture, 14, 8,12,12) }

    val tableNinePatch by lazy { Texture(Gdx.files.internal("ui/graphics/convobackground.png"))}
    val tableBackGround by lazy { NinePatch(tableNinePatch, 4, 4, 4, 4 ) }

    val portrait by lazy { Texture(Gdx.files.internal("portraits/portrait.png"))}

    val characters: Map<String, Map<AnimState, LpcCharacterAnim<TextureRegion>>> by lazy {
        SpriteLoader.initCharachterAnims()
    }

    val enemies by lazy {
        SpriteLoader.initEnemyAnims()
    }

    val bosses by lazy {
        SpriteLoader.initBossAnims()
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

    val enemyGibs by lazy {
        val texture = Texture(Gdx.files.internal("sprites/enemies/enemy_gibs.png"))
        listOf(
            TextureRegion(texture,0,0,24,24),
            TextureRegion(texture,24,0,24,24),
            TextureRegion(texture,48,0,24,24),
            TextureRegion(texture,72,0,24,24),
            TextureRegion(texture,96,0,24,24),
            TextureRegion(texture,0,24,24,24),
            TextureRegion(texture,24,24,24,24),
            TextureRegion(texture,48,24,24,24),
            TextureRegion(texture,72,24,24,24),
            TextureRegion(texture,96,24,24,24)
        )
    }

    private val handgunTexture by lazy {
        Texture(Gdx.files.internal("sprites/weapons/handgun.png"))
    }

    private val spas12Texture by lazy {
        Texture(Gdx.files.internal("sprites/weapons/spas-12.png"))
    }

    private val batTextureRegion by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/weapons/bat.png")), 0, 0,64, 10)
    }


    val weapons by lazy {
        mapOf(
            GunFrames.handGun to
                    mapOf(
                        SpriteDirection.North to
                                TextureRegion(handgunTexture, 0, 0,64, 64),
                        SpriteDirection.West to
                                TextureRegion(handgunTexture, 0, 64, 64, 64),
                        SpriteDirection.South to
                                TextureRegion(handgunTexture, 0, 128, 64, 64),
                        SpriteDirection.East to
                                TextureRegion(handgunTexture, 0, 192, 64,64)
                        ),
            GunFrames.spas12 to
                    mapOf(
                        SpriteDirection.North to
                                TextureRegion(spas12Texture, 0, 0,64, 64),
                        SpriteDirection.West to
                                TextureRegion(spas12Texture, 0, 64, 64, 64),
                        SpriteDirection.South to
                                TextureRegion(spas12Texture, 0, 128, 64, 64),
                        SpriteDirection.East to
                                TextureRegion(spas12Texture, 0, 192, 64,64)
                    ),
            GunFrames.bat to
                    mapOf(
                        SpriteDirection.North to
                                batTextureRegion,
                        SpriteDirection.West to
                                batTextureRegion,
                        SpriteDirection.South to
                                batTextureRegion,
                        SpriteDirection.East to
                                batTextureRegion,
                    )
        )
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
    val lootBox by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/loot/lootbox.png")))
    }
    val arrowTexture by lazy {
        Texture(Gdx.files.internal("sprites/arrows.png"))
    }

    val splatterEffectPool: ParticleEffectPool by lazy {
        val pe = ParticleEffect()
        pe.load(Gdx.files.internal("particles/blood_splatter/blood_splatter.effect"), Gdx.files.internal("particles/blood_splatter"))
        pe.scaleEffect(0.025f)
        ParticleEffectPool(pe, 1000, 3000)
    }

    val fireEffectPool: ParticleEffectPool by lazy {
        val pe = ParticleEffect()
        pe.load(Gdx.files.internal("particles/flame/pp_flame.p"), Gdx.files.internal("particles/flame"))
        pe.scaleEffect(0.025f)
        ParticleEffectPool(pe, 1000, 3000)
    }

    val arrows by lazy {
        mapOf(
            MapDirection.North to TextureRegion(arrowTexture, 0, 0, 16, 16),
            MapDirection.West to TextureRegion(arrowTexture, 16, 0, 16, 16),
            MapDirection.South to TextureRegion(arrowTexture, 16, 16, 16, 16),
            MapDirection.East to TextureRegion(arrowTexture, 0, 16, 16, 16)
        )
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
        tiles.filterKeys { it.contains("build")}.values.toList()
    }

    val aiDebugBadges by lazy {
        val texture = Texture(Gdx.files.internal("sprites/bt_labels/bt_labels.png"))
        mapOf(
            "amble" to TextureRegion(texture, 0,0, 64, 12),
            "attack" to TextureRegion(texture, 0,12, 64, 12),
            "chase" to TextureRegion(texture, 0,24, 64, 12),
            "check" to TextureRegion(texture, 0,36, 64, 12),
            "seek" to TextureRegion(texture, 0,48, 64, 12),
            "grabthrow" to TextureRegion(texture, 0,60, 64, 12),
            "rush" to TextureRegion(texture, 0,72, 64, 12),
            "panic" to TextureRegion(texture, 0,84, 64, 12)
        )
    }

    val ps4Buttons by lazy {
        val texture = Texture(Gdx.files.internal("controllers/PS4.png"))
        val y=  48
        val x = 32
        mapOf(
            "cross" to TextureRegion(texture, 32, 48, 16,16),
            "square" to TextureRegion(texture, 32, 64, 16,16),
            "triangle" to TextureRegion(texture, 32, 80, 16,16),
            "circle" to TextureRegion(texture, 32, 96, 16,16),
        )
    }
    val bullet by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/bullets/bullet.png")))
    }

    val playerCharacters by lazy { characters }

    val splashTexture: Texture by lazy {
        Texture(Gdx.files.internal("splash/splash_1.png"))
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
//        am.registerFreeTypeFontLoaders()
        fixScene2dSkin()
        fixFlip()
        return am
    }

    private fun fixFlip() {
        for (t in towers.values)
            t.flip(true, false)

        for(t in aiDebugBadges.values)
            t.flip(true, false)
    }

    private fun fixScene2dSkin() {
        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("skins/c64/uiskin.json"))
    }

    override fun dispose() {

    }
}