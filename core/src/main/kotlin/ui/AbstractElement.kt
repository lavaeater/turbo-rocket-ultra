package ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import tru.Assets

abstract class AbstractElement(
    val position: Vector2 = vec2(),
    var width: Float = 100f,
    var height: Float = 100f,
    parent: AbstractElement? = null) {

    var parent: AbstractElement? = parent
    set(value) {
        field = value
        updatePosition()
    }

    private val _actualPosition = vec2()
    open val actualPosition: Vector2
        get () {
            updatePosition()
        return _actualPosition
    }

    private fun updatePosition() {
        if (parent != null) {
            _actualPosition.set(
                parent!!.position.x + position.x,
                parent!!.position.y - position.y
            ) //WHat about coordinate system orientation? AAAH
        } else {
            _actualPosition.set(position)
        }
    }

    private val _bounds: Rectangle = Rectangle(actualPosition.x, actualPosition.y, width, height)
    open val bounds: Rectangle
        get() = _bounds
    val shapeDrawer by lazy { Assets.shapeDrawer }

    open fun renderBounds() {
        shapeDrawer.rectangle(bounds)
            shapeDrawer.filledCircle(centerVector.set(bounds.x, bounds.y), 3f, Color.RED)
            shapeDrawer.filledCircle(centerVector.set(bounds.x + bounds.width, bounds.y), 3f, Color.GREEN)
            shapeDrawer.filledCircle(centerVector.set(bounds.x + bounds.width, bounds.y + bounds.height), 3f, Color.BLUE)
            shapeDrawer.filledCircle(centerVector.set(bounds.x, bounds.y + bounds.height), 3f, Color.BLACK)
    }
    private val centerVector = vec2()

    open fun render(batch: Batch, delta:Float, debug: Boolean = false) {
        if (debug)
            renderBounds()
    }
}

