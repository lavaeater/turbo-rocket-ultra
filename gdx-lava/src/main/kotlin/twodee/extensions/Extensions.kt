package twodee.extensions

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import ktx.actors.onChange
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

fun traceRectangle(sprite: Sprite): Rectangle {
    val pixmap = getVisiblePixmap(sprite)
    val width = pixmap.width
    val height = pixmap.height

    // The rectangle is defined by (minX, minY) and (maxX, maxY)
    // The bottom leftmost pixel with a color is at (minX, minY)
    var minX = width
    var minY = height
    var maxX = 0
    var maxY = 0
    for (x in 0 until width) {
        for (y in 0 until height) {
            val color = pixmap.getPixel(x, y)
            if (color and 0x000000ff != 0) { // check if alpha is not 0
                minX = Math.min(minX, x)
                minY = Math.min(minY, y)
                maxX = Math.max(maxX, x)
                maxY = Math.max(maxY, y)
            }
        }
    }
    val rectangle = Rectangle(sprite.boundingRectangle)
    rectangle.setSize(maxX.toFloat() - minX, maxY.toFloat() - minY)
    rectangle.setX(minX.toFloat())
    rectangle.setY(minY.toFloat())
    pixmap.dispose()
    return rectangle
}

private fun getVisiblePixmap(sprite: Sprite): Pixmap {
    val texture = sprite.texture
    val data = texture.textureData
    if (!data.isPrepared) data.prepare()

    //this pixmap may be a texture atlas, so adjust it to only what the sprite sees
    val fullPixmap = data.consumePixmap()
    val visible = Pixmap(sprite.regionWidth, sprite.regionHeight, sprite.texture.textureData.format)
    visible.drawPixmap(fullPixmap, 0, 0, sprite.regionX, sprite.regionY, sprite.regionWidth, sprite.regionHeight)
    fullPixmap.dispose()
    return visible
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

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.boundTextField(
    noinline textFunction: () -> String,
    noinline textUpdated: (String) -> Unit,
    style: String = defaultStyle,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: (@Scene2dDsl TextField).(S) -> Unit = {}
): TextField {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(BoundTextField(textFunction, skin)
        .apply {
            onChange { textUpdated(text) }
        }, init
    )
}

open class BoundTextField(private val textFunction: () -> String, skin: Skin = Scene2DSkin.defaultSkin) :
    TextField(textFunction(), skin) {
    override fun act(delta: Float) {
        text = textFunction()
        super.act(delta)
    }
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