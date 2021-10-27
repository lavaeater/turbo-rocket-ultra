package tru

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import injection.Context.inject
import ktx.scene2d.Scene2DSkin
import space.earlygrey.shapedrawer.ShapeDrawer
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator


/**
 * Actually load assets using the asset manager, maan
 */
object Assets : Disposable {

    lateinit var am: AssetManager

    val characters: Map<String, Map<AnimState, LpcCharacterAnim>> by lazy {
        SpriteLoader.initCharachterAnims()
    }

    val towers by lazy {
        mapOf(
            "machinegun" to TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-gun.png"))),
            "flamethrower" to TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-gun.png"))),
            "obstacle" to TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-objective.png"))),
            "objective" to TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-objective.png"))),
            "noise" to TextureRegion(Texture(Gdx.files.internal("sprites/towers/tower-gun.png")))
        )
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

    val objectSprites by lazy { SpriteLoader.initObjectSprites() }

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
        for(t in towers.values)
            t.flip(true, false)
    }


    private fun fixScene2dSkin() {
        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("ui/uiskin.json"))
    }

    override fun dispose() {

    }
}