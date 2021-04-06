package ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import tru.Assets

abstract class AbstractActor(
    val position: Vector2 = vec2(),
    val width: Float = 100f,
    val height: Float = 100f,
    val parent: AbstractActor? = null) {

    private val _actualPosition = vec2()
    private val actualPosition: Vector2
        get () {
        if(parent != null) {
            _actualPosition.set(parent.position.x + position.x, parent.position.y + position.y) //WHat about coordinate system orientation? AAAH
        } else {
            _actualPosition.set(position)
        }
        return _actualPosition
    }

    private val _bounds: Rectangle = Rectangle(actualPosition.x, actualPosition.y, width, height)
    open val bounds: Rectangle
        get() = _bounds
    val shapeDrawer by lazy { Assets.shapeDrawer }
    fun renderBounds() {
        shapeDrawer.rectangle(bounds)
    }
    private val centerVector = vec2()

    open fun render(batch: Batch, debug: Boolean = false) {
        if (debug)
            renderBounds()

        shapeDrawer.filledCircle(bounds.getCenter(centerVector), width / 2, Color.GREEN)
    }
}

open class ActualActor: AbstractActor()