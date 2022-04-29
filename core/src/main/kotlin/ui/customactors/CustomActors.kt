package ui.customactors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable
import ktx.actors.txt
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

open class BoundLabel(private val textFunction: ()-> String, skin: Skin = Scene2DSkin.defaultSkin): Label(textFunction(), skin) {
    override fun act(delta: Float) {
        txt = textFunction()
        super.act(delta)
    }
}

open class BoundProgressBar(private val valueFunction: () -> Float, min: Float, max: Float, stepSize: Float, skin: Skin = Scene2DSkin.defaultSkin): ProgressBar(min, max, stepSize, false, skin) {
    override fun act(delta: Float) {
        value = valueFunction()
        super.act(delta)
    }
}

@OptIn(ExperimentalContracts::class)
@ktx.scene2d.Scene2dDsl
inline fun <S> KWidget<S>.repeatingTexture(noinline countFunction: () -> Int, spacing: Float = 5f, textureRegion: TextureRegion, init: (@ktx.scene2d.Scene2dDsl RepeatingTextureActor).(S) -> Unit): RepeatingTextureActor
{
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(RepeatingTextureActor(countFunction, spacing, textureRegion), init)
}


open class RepeatingTextureActor(private val countFunction: () -> Int, private val spacing: Float = 5f, textureRegion: TextureRegion) : Image(textureRegion) {
    override fun draw(batch: Batch, parentAlpha: Float) {
        validate()
        val color = color
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        val x = x
        val y = y
        val scaleX = scaleX
        val scaleY = scaleY
        if (drawable is TransformDrawable) {
            val rotation = rotation
            if (scaleX != 1f || scaleY != 1f || rotation != 0f) {
                for(index in 0 until countFunction()) {
                    val actualSpacing = if (index != 0) spacing else 0f
                    (drawable as TransformDrawable).draw(
                        batch, x + imageX + index * imageWidth * scaleX + actualSpacing, y + imageY, originX - imageX, originY - imageY,
                        imageWidth, imageHeight, scaleX, scaleY, rotation
                    )
                }
                return
            }
        }
        if (drawable != null) {
            for(index in 0 until countFunction()) {
                val actualSpacing = if (index != 0) spacing else 0f
                drawable.draw(batch, x + imageX + index * imageWidth + actualSpacing, y + imageY, imageWidth * scaleX, imageHeight * scaleY)
            }
        }
    }
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.boundProgressBar(
    noinline valueFunction: () -> Float,
    min: Float = 0f,
    max: Float = 1f,
    step: Float = 0.01f,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: (@Scene2dDsl BoundProgressBar).(S) -> Unit = {}
): BoundProgressBar {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(BoundProgressBar(valueFunction, min, max, step, skin), init)
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.boundLabel(
    noinline textFunction: () -> String,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: (@Scene2dDsl BoundLabel).(S) -> Unit = {}
): Label {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(BoundLabel(textFunction, skin), init)
}
