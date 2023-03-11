package common.extensions

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.actors.txt
import ktx.scene2d.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

fun TextureRegion.draw(batch: PolygonSpriteBatch, position: Vector2, rotation: Float, scale: Float) {
    batch.draw(
        this,
        position.x - this.regionWidth / 2f,
        position.y - this.regionHeight / 2f,
        this.regionWidth / 2f,
        this.regionHeight / 2f,
        this.regionWidth.toFloat(),
        this.regionHeight.toFloat(),
        scale,
        scale,
        rotation
    )
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


open class BoundLabel(private val textFunction: () -> String, skin: Skin = Scene2DSkin.defaultSkin) :
    Label(textFunction(), skin) {
    override fun act(delta: Float) {
        txt = textFunction()
        super.act(delta)
    }
}

open class BoundProgressBar(
    private val valueFunction: () -> Float,
    min: Float,
    max: Float,
    stepSize: Float,
    skin: Skin = Scene2DSkin.defaultSkin
) : ProgressBar(min, max, stepSize, false, skin) {
    override fun act(delta: Float) {
        value = valueFunction()
        super.act(delta)
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


fun Int.has(flag: Int) = flag and this == flag
fun Int.with(flag: Int) = this or flag
fun Int.without(flag: Int) = this and flag.inv()

fun Short.has(flag: Short) = flag and this == flag
fun Short.with(flag: Short) = this or flag
fun Short.without(flag: Short) = this and flag.inv()


//@Scene2dDsl
//@OptIn(ExperimentalContracts::class)
//inline fun <S> KWidget<S>.typingLabel(
//    text: CharSequence,
//    style: String = defaultStyle,
//    skin: Skin = Scene2DSkin.defaultSkin,
//    init: (@Scene2dDsl TypingLabel).(S) -> Unit = {}
//): TypingLabel {
//    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
//    return actor(TypingLabel(text, skin, style), init)
//}