package wastelandui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.math.vec2
import ktx.scene2d.*


inline fun <S> KWidget<S>.label(
    text: CharSequence,
    style: Label.LabelStyle,
    init: (@Scene2dDsl Label).(S) -> Unit = {}) = actor(Label(text, style), init)

inline fun <S> KWidget<S>.image(
		texture: Texture,
		init: (@Scene2dDsl Image).(S) -> Unit = {}) = actor(Image(TextureRegionDrawable(TextureRegion(texture))), init)

/**
 * @param text will be displayed as [TextButton] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [TextButton] instance added to this group.
 */
inline fun <S> KWidget<S>.textButton(
		text: String,
		style: String = defaultStyle,
		skin: Skin = Scene2DSkin.defaultSkin,
		init: KTextButton.(S) -> Unit = {}) = actor(KTextButton(text, skin, style), init)

fun Char.toNumber() : Int {
	if (this !in '0'..'9') {
		throw NumberFormatException()
	}
	return this - '0'
}

fun Vector3.toVec2(): Vector2 {
	return vec2(this.x, this.y)
}
