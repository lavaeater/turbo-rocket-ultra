package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
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
//        position.set(-5f, -5f, 0f)
    }
    override val viewport = FitViewport(16f, 16f, camera)
    var gridMap: MutableMap<Coordinate, SectionDefinition> = mutableMapOf(Coordinate(0, 0) to SectionDefinition.Start)
    val shapeDrawer by lazy { Assets.shapeDrawer }

    val testCoordinate = Coordinate(0, 0)

    val minX get() = gridMap.keys.minOf { it.x }
    val minY get() = gridMap.keys.minOf { it.y }
    val maxX get() = gridMap.keys.maxOf { it.x }
    val maxY get() = gridMap.keys.maxOf { it.y }

    var cursorX = 0
    var cursorY = 0

    var blinkTime = 0f
    var blink = false

    override fun render(delta: Float) {
//        camera.position.set(0f, 0f, 0f)
        super.render(delta)
        batch.use {
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    testCoordinate.x = x
                    testCoordinate.y = y
                    if (gridMap.containsKey(testCoordinate)) {
                        shapeDrawer.filledRectangle(x * 8f, y * 8f, 8f, 8f, gridMap[testCoordinate]!!.sectionColor)
                        //Render what it contains, somehow
                    } else {
                        //This particular pixel will be black
                    }
                }
            }

            blinkTime += delta
            if (blinkTime > 0.25) {
                blinkTime = 0f
                blink = true
            } else {
                blink = false
            }
            shapeDrawer.filledRectangle(cursorX * 8f, cursorY * 8f, 8f, 8f, if (blink) Color.WHITE else Color.BLACK)
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.UP -> cursorY++
            Input.Keys.DOWN -> cursorY--
            Input.Keys.LEFT -> cursorX--
            Input.Keys.RIGHT -> cursorX++
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