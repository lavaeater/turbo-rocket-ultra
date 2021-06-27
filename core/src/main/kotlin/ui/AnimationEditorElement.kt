package ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ktx.collections.toGdxArray
import ktx.math.vec2
import physics.drawScaled
import tru.AnimDef
import tru.AnimState
import tru.SpriteDirection


class AnimationEditorElement(
    private val texture: Texture,
    private val regionWidth: () -> Float,
    private val regionHeight: () -> Float,
    position: Vector2 = vec2(),
    parent: AbstractElement? = null
) : AbstractElement(position, parent = parent) {

    var column = 0
    var row = 0

    val maxCol get() = (texture.width / spriteWidth)
    val maxRow get() = (texture.height / spriteHeight)

    var startCol = 0
    var startRow = 0

    var endCol = 0
    var endRow = 0

    val spriteWidth get() = regionWidth().toInt()
    val spriteHeight get() = regionHeight().toInt()

    val defs = mutableListOf<AnimDef>()


    private val numberOfFrames: Int
        get() {
            return if (endRow == startRow && startCol == endCol) {
                1
            } else if (endRow == startRow) {
                endCol - startCol
            } else {
                ((endRow - startRow + 1) * maxCol) - startCol - endCol
            }
        }

    val actualRow get() = (maxRow - row) - 1

    fun handleInput(key: Int): Boolean {
        when (key) {
            Input.Keys.UP -> goUp()
            Input.Keys.DOWN -> goDown()
            Input.Keys.LEFT -> goLeft()
            Input.Keys.RIGHT -> goRight()
            Input.Keys.S -> markStart()
            Input.Keys.E -> markEnd()
            Input.Keys.ENTER -> saveAnim()
            Input.Keys.J -> nextState()
            Input.Keys.K -> nextDirection()
        }
        return true
    }

    private fun saveAnim() {
        defs.add(AnimDef(currentAnimState, currentDirection, row, startCol, endCol))

        val jsonString = Json.encodeToString(defs)

        //Wutwut
        val file = Gdx.files.local("files/sheet.json")
        file.writeString(jsonString, false)
    }

    var stateIndex = 0
    val currentAnimState get() = AnimState.animStates[stateIndex]
    var directionIndex = 0
    val currentDirection get() = SpriteDirection.spriteDirections[directionIndex]

    private fun nextState() {
        stateIndex++
        if (stateIndex >= AnimState.animStates.size)
            stateIndex = 0
    }

    private fun nextDirection() {
        directionIndex++
        if (directionIndex >= SpriteDirection.spriteDirections.size)
            directionIndex = 0

    }

    private fun markEnd() {
        endCol = column + 1
        endRow = row
        updateAnim()
    }

    private fun markStart() {
        startCol = column
        startRow = row
        updateAnim()
    }

    var regions = Array(maxCol) {
        TextureRegion(texture, it * spriteWidth, 0, spriteWidth, spriteHeight)
    }.toGdxArray()
    var anim = Animation(0.2f, regions, Animation.PlayMode.LOOP)

    private fun gridRowForY(y: Int): Int = maxRow - y - 1
    private fun spriteRowForY(y: Int): Int = y

    private fun updateAnim() {
        if (endRow < startRow)
            endRow = startRow
        if (endCol < startCol)
            endCol = startCol

        var x = startCol
        var y = startRow

        val array = Array(numberOfFrames) {
            val region =
                TextureRegion(texture, x * spriteWidth, spriteRowForY(y) * spriteHeight, spriteWidth, spriteHeight)
            x++
            if (x > maxCol) {
                x = 0
                y++
            }
            if (y > maxRow) //This can't happen, right?
                y = 0

            region
        }.toGdxArray()
        anim = Animation(0.2f, array, Animation.PlayMode.LOOP)
    }

    fun goUp() {
        row -= 1
        if (row < 0)
            row = 0
    }

    fun goDown() {
        row += 1
        if (row > maxRow)
            row = maxRow
    }

    fun goLeft() {
        column -= 1
        if (column < 0)
            column = 0
    }

    fun goRight() {
        column += 1
        if (column > maxCol)
            column = maxCol
    }

    var stateTime = 0f
    override fun render(batch: Batch, delta: Float, debug: Boolean) {
        super.render(batch, delta, debug)

        stateTime += delta
        val frame = anim.getKeyFrame(stateTime)
        batch.drawScaled(
            frame,
            actualPosition.x,
            actualPosition.y,
            1f,
            0f
        )
        var x = startCol
        var y = startRow

        for (frame in 0 until numberOfFrames) {
            shapeDrawer.filledRectangle(
                (x * spriteWidth).toFloat(),
                (gridRowForY(y) * spriteHeight).toFloat(),
                spriteWidth.toFloat(),
                spriteHeight.toFloat(),
                Color(0.7f, 0.7f, 1f, 0.5f)
            )
            x++
            if (x > maxCol) {
                x = 0
                y++
            }
            if (y > maxRow) //This can't happen, right?
                y = 0
        }

        shapeDrawer.filledRectangle(
            (column * spriteWidth).toFloat(),
            (actualRow * spriteHeight).toFloat(),
            spriteWidth.toFloat(),
            spriteHeight.toFloat(),
            Color(0.7f, 1f, 0.7f, 0.5f)
        )


    }
}

sealed class AnimEditMode {
    object Navigate : AnimEditMode()
}