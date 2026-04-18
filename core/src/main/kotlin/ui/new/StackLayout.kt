package ui.new

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

enum class StackAxis { HORIZONTAL, VERTICAL }

/**
 * Positions children sequentially along [axis] with [spacing] between them.
 * Children keep their own width/height; StackLayout computes its own size from them.
 */
class StackLayout(
    position: Vector2 = vec2(),
    val axis: StackAxis = StackAxis.VERTICAL,
    val spacing: Float = 0f,
    parent: AbstractElement? = null
) : ContainerElement(position, parent) {

    private fun layout() {
        var cursor = 0f
        for (child in childActors) {
            when (axis) {
                StackAxis.VERTICAL -> {
                    child.position.set(0f, cursor)
                    cursor += child.height + spacing
                }
                StackAxis.HORIZONTAL -> {
                    child.position.set(cursor, 0f)
                    cursor += child.width + spacing
                }
            }
        }
        when (axis) {
            StackAxis.VERTICAL -> {
                width = childActors.maxOfOrNull { it.width } ?: 0f
                height = if (childActors.isEmpty()) 0f else cursor - spacing
            }
            StackAxis.HORIZONTAL -> {
                width = if (childActors.isEmpty()) 0f else cursor - spacing
                height = childActors.maxOfOrNull { it.height } ?: 0f
            }
        }
    }

    override fun render(batch: Batch, delta: Float, scale: Float, debug: Boolean) {
        layout()
        super.render(batch, delta, scale, debug)
    }
}
