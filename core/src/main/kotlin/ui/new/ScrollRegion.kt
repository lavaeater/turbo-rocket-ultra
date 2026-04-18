package ui.new

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.math.vec2

/**
 * Clips [child] to a [viewportWidth] × [viewportHeight] window and renders it
 * offset by [scrollX] / [scrollY]. Call [scroll] to move the view.
 *
 * Requires a [viewport] for the coordinate conversion that ScissorStack needs.
 */
class ScrollRegion(
    position: Vector2 = vec2(),
    val viewportWidth: Float,
    val viewportHeight: Float,
    val child: AbstractElement,
    val viewport: Viewport,
    parent: AbstractElement? = null
) : AbstractElement(position, viewportWidth, viewportHeight, parent) {

    var scrollX: Float = 0f
        private set
    var scrollY: Float = 0f
        private set

    private val scissors = Rectangle()
    private val clipBounds = Rectangle()

    fun scroll(dx: Float, dy: Float) {
        val maxScrollX = (child.width - viewportWidth).coerceAtLeast(0f)
        val maxScrollY = (child.height - viewportHeight).coerceAtLeast(0f)
        scrollX = (scrollX + dx).coerceIn(0f, maxScrollX)
        scrollY = (scrollY + dy).coerceIn(0f, maxScrollY)
    }

    fun scrollTo(x: Float, y: Float) {
        scroll(x - scrollX, y - scrollY)
    }

    override fun render(batch: Batch, delta: Float, scale: Float, debug: Boolean) {
        val ax = actualPosition.x
        val ay = actualPosition.y

        clipBounds.set(ax, ay, viewportWidth * scale, viewportHeight * scale)
        com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.calculateScissors(
            viewport.camera, batch.transformMatrix, clipBounds, scissors
        )

        batch.flush()
        if (com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.pushScissors(scissors)) {
            child.position.set(ax - scrollX, ay - scrollY)
            child.parent = null
            child.render(batch, delta, scale, debug)
            batch.flush()
            com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.popScissors()
        }

        if (debug) renderBounds(scale)
    }
}
