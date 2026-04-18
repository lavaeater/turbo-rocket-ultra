package ui.new

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

/**
 * Arranges children left-to-right, wrapping to a new row when the next child
 * would exceed [maxWidth]. [hSpacing] is the gap between items on a row;
 * [vSpacing] is the gap between rows.
 */
class FlowLayout(
    position: Vector2 = vec2(),
    val maxWidth: Float = 400f,
    val hSpacing: Float = 4f,
    val vSpacing: Float = 4f,
    parent: AbstractElement? = null
) : ContainerElement(position, parent) {

    private fun layout() {
        var cursorX = 0f
        var cursorY = 0f
        var rowHeight = 0f

        for (child in childActors) {
            if (cursorX > 0f && cursorX + child.width > maxWidth) {
                cursorX = 0f
                cursorY += rowHeight + vSpacing
                rowHeight = 0f
            }
            child.position.set(cursorX, cursorY)
            cursorX += child.width + hSpacing
            if (child.height > rowHeight) rowHeight = child.height
        }

        width = maxWidth
        height = if (childActors.isEmpty()) 0f else cursorY + rowHeight
    }

    override fun render(batch: Batch, delta: Float, scale: Float, debug: Boolean) {
        layout()
        super.render(batch, delta, scale, debug)
    }
}
