package ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import kotlin.math.roundToInt

class BoundGridElement(
    width: Float,
    height: Float,
    var gridWidth: Float = 32f,
    var gridHeight: Float = 32f,
    var aspectLocked: Boolean = true,
    private val gridUpdated: (Int, Int)-> Unit = { _, _ -> },
    position: Vector2 = vec2(),
    parent: AbstractElement? = null) : AbstractElement(
    position,
    width = width,
    height = height,
    parent = parent) {
    init {
        updateGrid()
    }

    fun incrementGridWidth() {
        gridWidth += 1f
        if (gridWidth > width)
            gridWidth = width

        updateGrid()
    }
    fun decrementGridWidth() {
        gridWidth -= 1f
        if (gridWidth < 1f)
            gridWidth = 1f
        updateGrid()
    }

    fun incrementGridHeight() {
        gridHeight += 1f
        if (gridHeight > height)
            gridHeight = height
        updateGrid()
    }

    fun decrementGridHeight() {
        gridHeight -= 1f
        if (gridHeight < 1f)
            gridHeight = 1f
        updateGrid()
    }

    private fun updateGrid() {
        gridUpdated(gridWidth.roundToInt(), gridHeight.toInt())
    }

    override fun render(batch: Batch, delta: Float, debug: Boolean) {
        super.render(batch, delta, debug)

        val columns = (width / gridWidth).roundToInt()
        val rows = (height / gridHeight).roundToInt()

        for(c in 1..columns)
            shapeDrawer.line(vec2(c * gridWidth, 0f), vec2(c * gridWidth, rows * gridHeight), Color.BLUE)

        for(r in 1..rows)
            shapeDrawer.line(vec2(0f, r * gridHeight), vec2(columns * gridWidth, r * gridHeight), Color.BLUE)
    }
}