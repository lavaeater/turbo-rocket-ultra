package ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class AnimationEditorElement(
    private val texture: Texture,
    private val regionWidth: () -> Float,
    private val regionHeight: () -> Float,
    position: Vector2 = vec2(),
    parent: AbstractElement? = null
) : AbstractElement(position, parent = parent) {

    var column = 0
    var row = 0

}