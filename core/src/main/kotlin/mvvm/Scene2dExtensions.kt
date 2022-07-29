package mvvm

import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node
import ktx.scene2d.*

interface BindableWidget {
	val propertyName:String
	fun updateValue(newValue: Any)
}

interface CommandWidget {
	val command: () -> Unit
}

/**
 *
 */
class BindableLabel(override val propertyName: String,
                        skin: Skin = Scene2DSkin.defaultSkin,
                        style: String = defaultStyle) : Label("", skin, style), BindableWidget {
	override fun updateValue(newValue: Any) {
		setText(newValue.toString())
	}
}

inline fun <S> KWidget<S>.bindableLabel(
		propertyName: String,
		skin: Skin = Scene2DSkin.defaultSkin,
		style: String = defaultStyle,
		init: (@Scene2dDsl Label).(S) -> Unit = {}) = actor(BindableLabel(propertyName, skin, style), init)

/**
 * @param text will be displayed as [TextButton] text.
 * @param style name of the widget style. Defaults to [defaultStyle].
 * @param skin [Skin] instance that contains the widget style. Defaults to [Scene2DSkin.defaultSkin].
 * @param init will be invoked with the widget as "this". Consumes actor container (usually a [Cell] or [Node]) that
 * contains the widget. Might consume the actor itself if this group does not keep actors in dedicated containers.
 * Inlined.
 * @return a [TextButton] instance added to this group.
 */
inline fun <S> KWidget<S>.commandTextButton(
		text: String,
		noinline command: ()->Unit,
		style: String = defaultStyle,
		skin: Skin = Scene2DSkin.defaultSkin,
		init: CommandTextButton.(S) -> Unit = {}) = actor(CommandTextButton(text, command, skin, style), init)

class CommandTextButton(text: String, override val command:()->Unit, skin: Skin, style: String): TextButton(text, skin, style),
	CommandWidget
