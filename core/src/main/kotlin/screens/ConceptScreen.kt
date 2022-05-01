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

sealed class EditEvent {
    object EnterPaintMode : EditEvent()
    object ExitPaintMode : EditEvent()
    object ExitAltMode : EditEvent()
    object EnterAltMode : EditEvent()
    object EnterCameraMode : EditEvent()
    object ExitCameraMode : EditEvent()
    object EnterCommandMode : EditEvent()
    object ExitCommandMode : EditEvent()
}

sealed class EditState() {
    object Normal : EditState()
    object Paint : EditState()
    object Alt : EditState()
    object Camera : EditState()
    object Command : EditState()
}

fun command(name: String, init: CommandMap.() -> Unit): CommandMap {
    val command = CommandMap(name)
    command.init()
    return command
}

sealed class KeyPress {
    object Up : KeyPress()
    object Down : KeyPress()
}

class CommandMap(val name: String) {
    private val commands = mutableMapOf<Int, (KeyPress) -> Unit>()
    fun setUp(keycode: Int, up: () -> Unit) {
        setBoth(keycode, up, {})
    }

    fun setDown(keycode: Int, down: () -> Unit) {
        setBoth(keycode, {}, down)
    }

    fun setBoth(keycode: Int, up: () -> Unit, down: () -> Unit) {
        commands[keycode] = {
            when (it) {
                KeyPress.Down -> down()
                KeyPress.Up -> up()
            }
        }
    }

    fun execute(keycode: Int, keyPress: KeyPress): Boolean {
        if (commands.containsKey(keycode)) {
            commands[keycode]!!(keyPress)
            return true
        }
        return false
    }
}

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    private fun stateUpdated(state: EditState) {
        currentControlMap = when (state) {
            EditState.Alt -> altModeMap
            EditState.Camera -> cameraModeMap
            EditState.Command -> commandModeMap
            EditState.Normal -> normalCommandMap
            EditState.Paint -> paintModeMap
        }
    }

    private val normalCommandMap = command("Normal") {
        setUp(Input.Keys.UP) { cursorY++ }
        setUp(Input.Keys.DOWN) { cursorY-- }
        setUp(Input.Keys.LEFT) { cursorX-- }
        setUp(Input.Keys.RIGHT) { cursorX++ }
        setBoth(Input.Keys.Z, { cameraZoom = 0f }, { cameraZoom = 1f })
        setBoth(Input.Keys.X, { cameraZoom = 0f }, { cameraZoom = -1f })
        setDown(Input.Keys.ALT_LEFT) { machine.acceptEvent(EditEvent.EnterAltMode) }
        setDown(Input.Keys.CONTROL_LEFT) { machine.acceptEvent(EditEvent.EnterCommandMode) }
        setDown(Input.Keys.SHIFT_LEFT) { machine.acceptEvent(EditEvent.EnterCameraMode) }
        setDown(Input.Keys.C) { insert(SectionDefinition.Corridor) }
        setDown(Input.Keys.P) { insert(SectionDefinition.Start) }
        setDown(Input.Keys.B) { insert(SectionDefinition.Boss) }
        setDown(Input.Keys.G) { insert(SectionDefinition.Goal) }
        setDown(Input.Keys.L) { insert(SectionDefinition.Loot) }
        setDown(Input.Keys.H) { insert(SectionDefinition.HackingStation) }
        setDown(Input.Keys.A) { insert(SectionDefinition.PerimeterGoal) }
        setDown(Input.Keys.S) { insert(SectionDefinition.Spawner) }
    }

    private val paintModeMap = command("Paint") {
        setDown(Input.Keys.ALT_LEFT) { machine.acceptEvent(EditEvent.ExitPaintMode) }
        setDown(Input.Keys.CONTROL_LEFT) {
            machine.acceptEvent(EditEvent.EnterCommandMode)
        }
        setUp(Input.Keys.UP) {
            cursorY++
            insert(sectionToSet)
        }
        setUp(Input.Keys.DOWN) {
            cursorY--
            insert(sectionToSet)
        }
        setUp(Input.Keys.LEFT) {
            cursorX--
            insert(sectionToSet)
        }
        setUp(Input.Keys.RIGHT) {
            cursorX++
            insert(sectionToSet)
        }
    }

    private val commandModeMap = command("Command") {
        setUp(Input.Keys.CONTROL_LEFT) { machine.acceptEvent(EditEvent.ExitCommandMode) }
        setUp(Input.Keys.S) { saveMap() }
    }

    private val cameraModeMap = command("Camera") {
        setUp(Input.Keys.SHIFT_LEFT) {
            cameraMove.setZero()
            machine.acceptEvent(EditEvent.ExitCameraMode)
        }
        setBoth(Input.Keys.UP, { cameraMove.y = 0f }, { cameraMove.y = -1f })
        setBoth(Input.Keys.DOWN, { cameraMove.y = 0f }, { cameraMove.y = 1f })
        setBoth(Input.Keys.LEFT, { cameraMove.y = 0f }, { cameraMove.x = -1f })
        setBoth(Input.Keys.RIGHT, { cameraMove.y = 0f }, { cameraMove.x = 1f })
    }

    private val altModeMap = command("Alt") {
        setUp(Input.Keys.ALT_LEFT) { machine.acceptEvent(EditEvent.ExitAltMode) }
        setDown(Input.Keys.C) {
            sectionToSet = SectionDefinition.Corridor
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.P) {
            sectionToSet = SectionDefinition.Start
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.B) {
            sectionToSet = SectionDefinition.Boss
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.G) {
            sectionToSet = SectionDefinition.Goal
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.L) {
            sectionToSet = SectionDefinition.Loot
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.H) {
            sectionToSet = SectionDefinition.HackingStation
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.A) {
            sectionToSet = SectionDefinition.PerimeterGoal
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.S) {
            sectionToSet = SectionDefinition.Spawner
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
    }

    private var currentControlMap: CommandMap = normalCommandMap

    private val machine: StateMachine<EditState, EditEvent> =
        StateMachine.buildStateMachine<EditState, EditEvent>(EditState.Normal, ::stateUpdated) {
            state(EditState.Normal) {
                edge(EditEvent.EnterCameraMode, EditState.Camera) {}
                edge(EditEvent.EnterAltMode, EditState.Alt) {}
                edge(EditEvent.EnterCommandMode, EditState.Command) {}
            }
            state(EditState.Command) {
                edge(EditEvent.ExitCommandMode, EditState.Normal) {}
            }
            state(EditState.Camera) {
                edge(EditEvent.ExitCameraMode, EditState.Normal) {}
            }
            state(EditState.Alt) {
                edge(EditEvent.ExitAltMode, EditState.Normal) {}
                edge(EditEvent.EnterPaintMode, EditState.Paint) {}
            }
            state(EditState.Paint) {
                edge(EditEvent.ExitPaintMode, EditState.Normal) {}
                edge(EditEvent.EnterCommandMode, EditState.Command) {}
            }
        }.apply {
            initialize()
        }


    private var cameraZoom: Float = 0f
    override val camera = OrthographicCamera().apply {
        setToOrtho(false)
    }
    override val viewport = FitViewport(32f, 32f, camera)
    private var gridMap: MutableMap<Coordinate, SectionDefinition> = mutableMapOf(Coordinate(0, 0) to SectionDefinition.Start)
    val shapeDrawer by lazy { Assets.shapeDrawer }

    private val gridSz = 33f
    private val squarSz = gridSz - 1f

    private val testCoordinate = Coordinate(0, 0)

    private val minX get() = gridMap.keys.minOf { it.x }
    private val minY get() = gridMap.keys.minOf { it.y }
    private val maxX get() = gridMap.keys.maxOf { it.x }
    private val maxY get() = gridMap.keys.maxOf { it.y }

    private var cursorX = 0
    private var cursorY = 0

    private var blinkTime = 0f
    private var blink = false
    private val blinkOn = Color(1f, 1f, 1f, 0.5f)
    private val blinkOff = Color(0f, 1f, 0f, 0.25f)
    private val zoomFactor = 0.05f

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
        return currentControlMap.execute(keycode, KeyPress.Down)
    }

    private var sectionToSet: SectionDefinition = SectionDefinition.Corridor

    private fun saveMap() {
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

    private val cameraMove = vec2()
    val maxWidth = 60
    val maxHeight = 60

    override fun keyUp(keycode: Int): Boolean {
        currentControlMap.execute(keycode, KeyPress.Up)
        if (cursorY < 0)
            cursorY = maxHeight
        if (cursorY > maxHeight)
            cursorY = 0
        if (cursorX < 0)
            cursorX = maxWidth
        if (cursorX > maxWidth)
            cursorX = 0

        return true
    }
}