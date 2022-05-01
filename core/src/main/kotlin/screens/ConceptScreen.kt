package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.vec2
import map.grid.Coordinate
import statemachine.StateMachine
import tru.Assets

sealed class SectionDefinition(val sectionColor: Color) {
    object Boss : SectionDefinition(Color.RED)
    object Loot : SectionDefinition(Color.WHITE)
    object Start : SectionDefinition(Color.BLUE)
    object Goal : SectionDefinition(Color.GREEN)
    object Spawner : SectionDefinition(Color.CYAN)
    object PerimeterGoal : SectionDefinition(Color.ORANGE)
    object HackingStation : SectionDefinition(Color.PURPLE)
    object Corridor : SectionDefinition(Color.GRAY)
}

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    override val camera = OrthographicCamera().apply {
        setToOrtho(false)
//        position.set(-0.1f, -0.1f, 0f)
    }
    override val viewport = FitViewport(32f, 32f, camera)
    var gridMap: MutableMap<Coordinate, SectionDefinition> = mutableMapOf(Coordinate(0, 0) to SectionDefinition.Start)
    val shapeDrawer by lazy { Assets.shapeDrawer }

    val gridSz = 33f
    val squarSz = gridSz - 1f

    val testCoordinate = Coordinate(0, 0)

    val minX get() = gridMap.keys.minOf { it.x }
    val minY get() = gridMap.keys.minOf { it.y }
    val maxX get() = gridMap.keys.maxOf { it.x }
    val maxY get() = gridMap.keys.maxOf { it.y }

    var cursorX = 0
    var cursorY = 0

    var blinkTime = 0f
    var blink = false
    val blinkOn = Color(1f,1f,1f,0.5f)
    val blinkOff = Color(0f,1f,0f,0.25f)
    var cameraMode = false

    override fun render(delta: Float) {
        camera.position.x += cameraMove.x
        camera.position.y += cameraMove.y
        super.render(delta)
        batch.use {
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    testCoordinate.x = x
                    testCoordinate.y = y
                    if (gridMap.containsKey(testCoordinate)) {
                        shapeDrawer.filledRectangle(x * gridSz, y * gridSz, squarSz, squarSz, gridMap[testCoordinate]!!.sectionColor)
                        //Render what it contains, somehow
                    } else {
                        //This particular pixel will be black
                    }
                }
            }

            blinkTime += delta
            if (blinkTime > 0.1) {
                blinkTime = 0f
                blink = true
            } else {
                blink = false
            }
            shapeDrawer.filledRectangle(cursorX * gridSz, cursorY * gridSz, squarSz, squarSz, if (blink) blinkOn else blinkOff)
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.SHIFT_LEFT -> cameraMode = true
            Input.Keys.SHIFT_RIGHT -> cameraMode = true
            Input.Keys.UP -> if(cameraMode) cameraMove.y = -1f
            Input.Keys.DOWN -> if(cameraMode) cameraMove.y = 1f
            Input.Keys.LEFT -> if(cameraMode) cameraMove.x = -1f
            Input.Keys.RIGHT -> if(cameraMode) cameraMove.x = 1f
            Input.Keys.C -> insert(SectionDefinition.Corridor)
            Input.Keys.B -> insert(SectionDefinition.Boss)
            Input.Keys.G -> insert(SectionDefinition.Goal)
            Input.Keys.L -> insert(SectionDefinition.Loot)
            Input.Keys.H -> insert(SectionDefinition.HackingStation)
            Input.Keys.P -> insert(SectionDefinition.PerimeterGoal)
            Input.Keys.S -> insert(SectionDefinition.Spawner)
            Input.Keys.FORWARD_DEL -> delete()
            Input.Keys.DEL -> delete()
            else -> return false
        }
        return true
    }

    private fun delete() {
        if(gridMap.count() > 1)
            gridMap.remove(Coordinate(cursorX, cursorY))
    }

    private fun insert(sectionType: SectionDefinition) {
        val coordinate = Coordinate(cursorX, cursorY)
        gridMap[coordinate] = sectionType
    }

    val cameraMove = vec2()

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.UP -> if(cameraMode) cameraMove.y = 0f else cursorY++
            Input.Keys.DOWN -> if(cameraMode) cameraMove.y = 0f else cursorY--
            Input.Keys.LEFT -> if(cameraMode) cameraMove.x = 0f else cursorX--
            Input.Keys.RIGHT -> if(cameraMode) cameraMove.x = 0f else cursorX++
            Input.Keys.SHIFT_LEFT -> cameraMode = false
            Input.Keys.SHIFT_RIGHT -> cameraMode = false
            else -> return false
        }

        if (cursorY < 0)
            cursorY = 20
        if (cursorY > 20)
            cursorY = 0
        if (cursorX < 0)
            cursorX = 20
        if (cursorX > 20)
            cursorX = 0
        return true
    }
}