package ui.simple

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import tru.Assets

class DataBoundMeter(
    val valueFunction: () -> Float,
    maxValue: Float,
    val width: Float,
    val height: Float,
    val position: Vector2 = vec2()
) :
    SimpleActor {

    private val shapeDrawer by lazy { Assets.shapeDrawer }

    private val ratio = width / maxValue

    override fun render(batch: Batch, parentPosition: Vector2, debug: Boolean) {
        //1. Draw meter
        // Meter is used in percentage. So max value = full width. calculate ratio and then calculate width of meter
        val meterWidth = valueFunction() * ratio
        shapeDrawer.filledRectangle(parentPosition.x + position.x, parentPosition.y - position.y - height, width, height, Color.BLUE)
        shapeDrawer.filledRectangle(parentPosition.x + position.x, parentPosition.y - position.y - height, meterWidth, height, Color.RED)
        //2. Draw outline

    }

}
