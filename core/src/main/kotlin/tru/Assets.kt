package tru

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import ktx.scene2d.Scene2DSkin

/**
 * Actually load assets using the asset manager, maan
 */
object Assets : Disposable {

    lateinit var am: AssetManager
    val characters :  Map<String, Map<AnimState, LpcCharacterAnim>> by lazy {
        CharacterSpriteLoader.initCharachterAnims()
    }

    val soundEffects : Map<String, Sound> by lazy {
        mapOf(
            "gunshot" to Gdx.audio.newSound(Gdx.files.internal("audio/gunshot.wav")),
            "shellcasing" to Gdx.audio.newSound(Gdx.files.internal("audio/shellcasing.wav"))
        )
    }

    fun load(): AssetManager {
        am = AssetManager()
        fixScene2dSkin()
        return am
    }


    private fun fixScene2dSkin() {
        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("ui/uiskin.json"))
    }

    override fun dispose() {

    }
}