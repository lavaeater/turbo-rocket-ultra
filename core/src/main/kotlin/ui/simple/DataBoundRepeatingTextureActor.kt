package ui.simple

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

open class DataBoundRepeatingTextureActor(
    val repeaterValue: () -> Int,
    offset: Vector2,
    textureRegion: TextureRegion,
    scale: Float) : RepeatingTextureActor(
    repeaterValue(),
    offset,
    textureRegion,
    scale
) {

    override val repeatFor get() = repeaterValue()
}