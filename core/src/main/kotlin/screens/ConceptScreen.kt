package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.viewport.FitViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.vec2
import map.grid.Coordinate
import statemachine.StateMachine
import tru.Assets
import ktx.json.*

sealed class SectionDefinition(val sectionColor: Color, val key: String) {
    object Boss : SectionDefinition(Color.RED, "b")
    object Loot : SectionDefinition(Color.WHITE, "l")
    object Start : SectionDefinition(Color.BLUE, "s")
    object Goal : SectionDefinition(Color.GREEN, "g")
    object Spawner : SectionDefinition(Color.CYAN, "w")
    object PerimeterGoal : SectionDefinition(Color.ORANGE, "p")
    object HackingStation : SectionDefinition(Color.PURPLE, "h")
    object Corridor : SectionDefinition(Color.GRAY, "c")
}

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    private var paintMode = false
    private var altMode = false
    private var cameraZoom: Float = 0f
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
    val blinkOn = Color(1f, 1f, 1f, 0.5f)
    val blinkOff = Color(0f, 1f, 0f, 0.25f)
    var cameraMode = false
    var commandMode = false
    val zoomFactor = 0.05f

    override fun render(delta: Float) {
        camera.position.x += cameraMove.x
        camera.position.y += cameraMove.y
        camera.zoom += zoomFactor * cameraZoom
        super.render(delta)
        batch.use {
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    testCoordinate.x = x
                    testCoordinate.y = y
                    if (gridMap.containsKey(testCoordinate)) {
                        shapeDrawer.filledRectangle(
                            x * gridSz,
                            y * gridSz,
                            squarSz,
                            squarSz,
                            gridMap[testCoordinate]!!.sectionColor
                        )
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
            shapeDrawer.filledRectangle(
                cursorX * gridSz,
                cursorY * gridSz,
                squarSz,
                squarSz,
                if (blink) blinkOn else blinkOff
            )
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ALT_LEFT -> altMode = true
            Input.Keys.CONTROL_LEFT -> commandMode = true
            Input.Keys.SHIFT_LEFT -> cameraMode = true
            Input.Keys.SHIFT_RIGHT -> cameraMode = true
            Input.Keys.UP -> if (cameraMode) cameraMove.y = -1f
            Input.Keys.DOWN -> if (cameraMode) cameraMove.y = 1f
            Input.Keys.LEFT -> if (cameraMode) cameraMove.x = -1f
            Input.Keys.RIGHT -> if (cameraMode) cameraMove.x = 1f
            Input.Keys.Z -> cameraZoom = 1f
            Input.Keys.X -> cameraZoom = -1f
            Input.Keys.C -> if(altMode) setPaintMode(SectionDefinition.Corridor) else insert(SectionDefinition.Corridor)
            Input.Keys.H -> if(altMode) setPaintMode(SectionDefinition.Start) else insert(SectionDefinition.Start)
            Input.Keys.B -> if(altMode) setPaintMode(SectionDefinition.Boss) else insert(SectionDefinition.Boss)
            Input.Keys.G -> if(altMode) setPaintMode(SectionDefinition.Goal) else insert(SectionDefinition.Goal)
            Input.Keys.L -> if(altMode) setPaintMode(SectionDefinition.Loot) else insert(SectionDefinition.Loot)
            Input.Keys.H -> if(altMode) setPaintMode(SectionDefinition.HackingStation) else insert(SectionDefinition.HackingStation)
            Input.Keys.P -> if(altMode) setPaintMode(SectionDefinition.PerimeterGoal) else insert(SectionDefinition.PerimeterGoal)
            Input.Keys.S -> if (commandMode) saveMap() else if(altMode) setPaintMode(SectionDefinition.Spawner) else insert(SectionDefinition.Spawner)
            Input.Keys.FORWARD_DEL -> delete()
            Input.Keys.DEL -> delete()
            else -> return false
        }
        return true
    }

    var sectionToSet: SectionDefinition = SectionDefinition.Corridor
    private fun setPaintMode(section: SectionDefinition) {
        sectionToSet = section
        paintMode = !paintMode
    }

    private fun saveMap() {
        //serialize it to a file
        val json = Json()

        json.addClassTag<SectionDefinition.Start>("start")
        json.addClassTag<SectionDefinition.Boss>("boss")
        json.addClassTag<SectionDefinition.Goal>("goal")
        json.addClassTag<SectionDefinition.Loot>("loot")
        json.addClassTag<SectionDefinition.Corridor>("corr")
        json.addClassTag<SectionDefinition.HackingStation>("hack")
        json.addClassTag<SectionDefinition.PerimeterGoal>("peri")
        json.addClassTag<SectionDefinition.Spawner>("spawn")
        json.addClassTag<Coordinate>("coord")
        var mapAsString = ""
        val currentC = Coordinate(minX, minY)
        for (y in maxY.downTo(minY)) {
            for (x in minX..maxX) {
                currentC.set(x, y)
                mapAsString += if (gridMap.containsKey(currentC))
                    gridMap[currentC]!!.key
                else
                    "*"
            }
            mapAsString += "\n"
        }
        val handle = Gdx.files.local("new_map.txt")
        handle.writeString(mapAsString, false)
    }

    private fun delete() {
        if (gridMap.count() > 1)
            gridMap.remove(Coordinate(cursorX, cursorY))
    }

    private fun insert(sectionType: SectionDefinition) {
        val coordinate = Coordinate(cursorX, cursorY)
        gridMap[coordinate] = sectionType
    }

    val cameraMove = vec2()

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ALT_LEFT -> altMode = false
            Input.Keys.CONTROL_LEFT -> commandMode = false
            Input.Keys.UP -> if (cameraMode) cameraMove.y = 0f else cursorY++
            Input.Keys.DOWN -> if (cameraMode) cameraMove.y = 0f else cursorY--
            Input.Keys.LEFT -> if (cameraMode) cameraMove.x = 0f else cursorX--
            Input.Keys.RIGHT -> if (cameraMode) cameraMove.x = 0f else cursorX++
            Input.Keys.SHIFT_LEFT -> cameraMode = false
            Input.Keys.SHIFT_RIGHT -> cameraMode = false
            Input.Keys.Z -> cameraZoom = 0f
            Input.Keys.X -> cameraZoom = 0f
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

        if(paintMode)
            insert(sectionToSet)

        return true
    }
}