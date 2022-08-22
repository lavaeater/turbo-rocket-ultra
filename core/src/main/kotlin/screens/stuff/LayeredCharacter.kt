package screens.stuff

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.math.vec2

class LayeredCharacter {
    val position = vec2() //This is the center of the character, then head should be about one "meter" above this.
    val headOffset = vec2(0f, 0.8f)
    val leftEye = vec2(0.1f)
    val rightEye = vec2(-0.1f)
    val head by lazy { TextureRegion(Texture(Gdx.files.internal("sprites/layered/head.png"))) }
    val eye by lazy { TextureRegion(Texture(Gdx.files.internal("sprites/layered/eye.png"))) }
    fun draw(batch: Batch, delta: Float) {

    }
}