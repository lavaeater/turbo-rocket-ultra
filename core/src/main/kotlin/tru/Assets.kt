package tru

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import ecs.components.graphics.OffsetTextureRegion
import ecs.components.player.GunFrames
import injection.Context.inject
import ktx.scene2d.Scene2DSkin
import map.snake.MapDirection
import space.earlygrey.shapedrawer.ShapeDrawer


/**
 * Actually load assets using the asset manager, maan
 */
object Assets : Disposable {

    lateinit var am: AssetManager

    val characters: Map<String, Map<AnimState, LpcCharacterAnim<TextureRegion>>> by lazy {
        SpriteLoader.initCharachterAnims()
    }

    val handgunTexture by lazy {
        Texture(Gdx.files.internal("sprites/guns/handgun.png"))
    }

    val spas12Texture by lazy {
        Texture(Gdx.files.internal("sprites/guns/spas-12.png"))
    }


    val weapons by lazy {
        mapOf(
            GunFrames.handGun to
                    mapOf(
                        SpriteDirection.North to
                                OffsetTextureRegion(handgunTexture, 0, 0,64, 64, 0f, 0f),
                        SpriteDirection.West to
                                OffsetTextureRegion(handgunTexture, 0, 64, 64, 64, 0f, 0f),
                        SpriteDirection.South to
                                OffsetTextureRegion(handgunTexture, 0, 128, 64, 64, 0f,0f),
                        SpriteDirection.West to
                                OffsetTextureRegion(handgunTexture, 0, 172, 64,64, 0f, 0f)
                        ),
            GunFrames.spas12 to
                    mapOf(
                        SpriteDirection.North to
                                OffsetTextureRegion(spas12Texture, 0, 0,64, 64, 0f, 0f),
                        SpriteDirection.West to
                                OffsetTextureRegion(spas12Texture, 0, 64, 64, 64, 0f, 0f),
                        SpriteDirection.South to
                                OffsetTextureRegion(spas12Texture, 0, 128, 64, 64, 0f,0f),
                        SpriteDirection.West to
                                OffsetTextureRegion(spas12Texture, 0, 172, 64,64, 0f, 0f)
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

    val arrowTexture by lazy {
        Texture(Gdx.files.internal("sprites/arrows.png"))
    }

    val splatterEffectPool: ParticleEffectPool by lazy {
        val pe = ParticleEffect()
        pe.load(Gdx.files.internal("particles/blood_splatter.effect"), Gdx.files.internal("particles/"))
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
                            2 -> ts["wall_end"] = TextureRegion(tileTexture, xTile * 16, yTile * 16, 16, 16)
                        }
                    }
                    2 -> {
                        if (xTile in 0..1)
                            ts["wall_shadow$xTile"] = TextureRegion(tileTexture, xTile * 16, yTile * 16, 16, 16)
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

    val wallEndShadow by lazy { tiles["wall_shadow1"]!! }
    val wallShadow by lazy { tiles["wall_shadow0"]!! }

    val towerShadow by lazy {
        TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-shadow.png")))
    }

    val playerCharacters by lazy { characters.filterNot { it.key == "enemy" } }

    val splashTexture: Texture by lazy {
        Texture(Gdx.files.internal("splash/splash_1.png"))
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

    fun load(): AssetManager {
        am = AssetManager()
        fixScene2dSkin()
        fixFlip()
        return am
    }

    private fun fixFlip() {
        for (t in towers.values)
            t.flip(true, false)

        towerShadow.flip(true, false)

    }


    private fun fixScene2dSkin() {
        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("ui/uiskin.json"))
    }

    override fun dispose() {

    }
}