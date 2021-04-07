package ui.simple

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class BoundTextActor(val textFunction: () -> String, position: Vector2 = vec2()) : TextActor(textFunction(), position) {
    override val text get() = textFunction()
}