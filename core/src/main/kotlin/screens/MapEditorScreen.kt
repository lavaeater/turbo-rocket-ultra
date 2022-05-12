package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.actors.onKeyDown
import ktx.actors.setKeyboardFocus
import ktx.graphics.use
import ktx.math.vec2
import ktx.scene2d.*
import map.grid.Coordinate
import statemachine.StateMachine
import tru.Assets
import ui.customactors.boundLabel

fun command(name: String, init: CommandMap.() -> Unit): CommandMap {
    val command = CommandMap(name)
    command.init()
    return command
}

class MapEditorScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    private fun stateUpdated(state: EditState, event: EditEvent?) {
        currentControlMap = when (state) {
            EditState.Alt -> altModeMap
            EditState.Camera -> cameraModeMap
            EditState.Command -> commandModeMap
            EditState.Normal -> normalCommandMap
            EditState.Paint -> paintModeMap
            EditState.DialogMode -> dialogModeMap
        }
    }

    private val normalCommandMap = command("Normal") {
        setUp(Input.Keys.DEL, "Delete") { delete() }
        setUp(Input.Keys.UP, "Move Up") { cursorY++ }
        setUp(Input.Keys.DOWN, "Move Down") { cursorY-- }
        setUp(Input.Keys.LEFT, "Move Left") { cursorX-- }
        setUp(Input.Keys.RIGHT, "Move Right") { cursorX++ }
        setBoth(Input.Keys.Z, "Zoom out", { cameraZoom = 0f }, { cameraZoom = 1f })
        setBoth(Input.Keys.X, "Zoom in", { cameraZoom = 0f }, { cameraZoom = -1f })
        setDown(Input.Keys.ALT_LEFT, "Alt Mode") { machine.acceptEvent(EditEvent.EnterAltMode) }
        setDown(Input.Keys.CONTROL_LEFT, "Command Mode") { machine.acceptEvent(EditEvent.EnterCommandMode) }
        setDown(Input.Keys.SHIFT_LEFT, "Camera Mode") { machine.acceptEvent(EditEvent.EnterCameraMode) }
        setDown(Input.Keys.C, "Corridor") { insert(SectionDefinition.Corridor) }
        setDown(Input.Keys.P, "Start") { insert(SectionDefinition.Start) }
        setDown(Input.Keys.B, "Boss") { insert(SectionDefinition.Boss) }
        setDown(Input.Keys.G, "Goal") { insert(SectionDefinition.Goal) }
        setDown(Input.Keys.L, "Loot") { insert(SectionDefinition.Loot) }
        setDown(Input.Keys.H, "Hacking") { insert(SectionDefinition.HackingStation) }
        setDown(Input.Keys.A, "Perimeter") { insert(SectionDefinition.PerimeterGoal) }
        setDown(Input.Keys.S, "Spawner") { insert(SectionDefinition.Spawner) }
    }

    private var isDirty = false

    private val paintModeMap = command("Paint") {
        setDown(Input.Keys.ALT_LEFT, "Paint Mode") { machine.acceptEvent(EditEvent.ExitPaintMode) }
        setDown(Input.Keys.CONTROL_LEFT, "Command Mode") {
            machine.acceptEvent(EditEvent.EnterCommandMode)
        }
        setUp(Input.Keys.UP, "Paint Up") {
            cursorY++
            insert(sectionToSet)
        }
        setUp(Input.Keys.DOWN, "Paint Down") {
            cursorY--
            insert(sectionToSet)
        }
        setUp(Input.Keys.LEFT, "Paint Left") {
            cursorX--
            insert(sectionToSet)
        }
        setUp(Input.Keys.RIGHT, "Paint Right") {
            cursorX++
            insert(sectionToSet)
        }
    }

    private val commandModeMap = command("Command") {
        setUp(Input.Keys.CONTROL_LEFT, "Command Mode") { machine.acceptEvent(EditEvent.ExitCommandMode) }
        setUp(Input.Keys.X, "Exit Editor") { exitEditor() }
        setUp(Input.Keys.F, "Set File Name") { setFileName() }
        setUp(Input.Keys.S, "Save Map") { saveMap() }
        setUp(Input.Keys.N, "New Map") { newMap() }
    }

    var fileName = "map"
    lateinit var fileNameTextField: TextField

    val fileNameDialog by lazy {
        val d = scene2d.dialog("Set name of map file") {
            label("File:")
            fileNameTextField = textField(fileName) {
                setKeyboardFocus(true)
                onKeyDown {
                    if (it == Input.Keys.ENTER) {
                        fileName = this.text
                    }
                    if (it == Input.Keys.ENTER || it == Input.Keys.ESCAPE) {
                        Gdx.input.inputProcessor = previousProcessor
                        this@dialog.isVisible = false
                    }
                }
            }
            pack()
            isVisible = false
        }
        stage.addActor(d)
        d.setPosition(stage.width / 2 - dialog.width / 2, stage.height / 2 - dialog.height / 2)
        d
    }
    private val fileNameCommandMap = command("FileName") {
        setDown(Input.Keys.ENTER, "Set Name") {
            fileName = fileNameTextField.text
            fileNameDialog.isVisible = false
            currentControlMap = commandModeMap
        }
        setDown(Input.Keys.ESCAPE, "Cancel") {
            fileNameDialog.isVisible = false
            currentControlMap = commandModeMap
        }
    }
    var previousProcessor = Gdx.input.inputProcessor

    private fun setFileName() {
        fileNameDialog.isVisible = true
        previousProcessor = Gdx.input.inputProcessor
        Gdx.input.inputProcessor = stage
    }

    private val dialogModeMap = command("Dialog") {
        setDown(Input.Keys.Y, "Yes") { executeDialogYes() }
        setDown(Input.Keys.N, "No") { executeDialogNo() }
    }
    var executeDialogYes: () -> Unit = {}
    var executeDialogNo: () -> Unit = {}
    private val dialog by lazy {
        val d = scene2d.dialog("Save changes") {
            center()
            label("This map has changes,\ndo you wish to save them? (Y/N)")
            pack()
            isVisible = false
        }
        stage.addActor(d)
        d.setPosition(stage.width / 2 - d.width / 2, stage.height / 2 - d.height / 2)
        d
    }

    private fun handleDirty(exit: Boolean = false) {
        if (isDirty) {
            executeDialogYes = {
                dialog.isVisible = false
                saveMap()
            }
            executeDialogNo = {
                dialog.isVisible = false
            }
            currentControlMap = dialogModeMap
            dialog.isVisible = true
        }
        if (exit) reallyExitEditor() else reallyNewMap()
    }

    private fun newMap() {
        handleDirty(false)
    }

    private fun reallyNewMap() {
        val keys = gridMap.keys.toTypedArray()
        for ((index, key) in keys.withIndex()) {
            if (index > 0) {
                gridMap.remove(key)
            }
        }
        isDirty = false
        mapIndex++
        machine.acceptEvent(EditEvent.ExitCommandMode)
    }

    private fun exitEditor() {
        handleDirty(true)
    }

    private fun reallyExitEditor() {
        machine.acceptEvent(EditEvent.ExitCommandMode)
        gameState.acceptEvent(GameEvent.ExitMapEditor)
    }

    private val cameraModeMap = command("Camera") {
        setUp(Input.Keys.SHIFT_LEFT, "Camera Mode") {
            cameraMove.setZero()
            machine.acceptEvent(EditEvent.ExitCameraMode)
        }
        setBoth(Input.Keys.UP, "Camera Up", { cameraMove.y = 0f }, { cameraMove.y = -1f })
        setBoth(Input.Keys.DOWN, "Camera Down", { cameraMove.y = 0f }, { cameraMove.y = 1f })
        setBoth(Input.Keys.LEFT, "Camera Left", { cameraMove.y = 0f }, { cameraMove.x = -1f })
        setBoth(Input.Keys.RIGHT, "Camera Right", { cameraMove.y = 0f }, { cameraMove.x = 1f })
    }

    private val altModeMap = command("Alt") {
        setUp(Input.Keys.ALT_LEFT, "Alt Mode") { machine.acceptEvent(EditEvent.ExitAltMode) }
        setDown(Input.Keys.C, "Paint Corridor") {
            sectionToSet = SectionDefinition.Corridor
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.P, "Paint Start") {
            sectionToSet = SectionDefinition.Start
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.B, "Paint Boss") {
            sectionToSet = SectionDefinition.Boss
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.G, "Paint Goal") {
            sectionToSet = SectionDefinition.Goal
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.L, "Paint Loot") {
            sectionToSet = SectionDefinition.Loot
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.H, "Paint Hacking") {
            sectionToSet = SectionDefinition.HackingStation
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.A, "Paint Perimeter Goal") {
            sectionToSet = SectionDefinition.PerimeterGoal
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
        setDown(Input.Keys.S, "Paint Spawner") {
            sectionToSet = SectionDefinition.Spawner
            machine.acceptEvent(EditEvent.EnterPaintMode)
        }
    }

    private val machine: StateMachine<EditState, EditEvent> =
        StateMachine.buildStateMachine<EditState, EditEvent>(EditState.Normal, ::stateUpdated) {
            state(EditState.Normal) {
                edge(EditEvent.EnterCameraMode, EditState.Camera) {}
                edge(EditEvent.EnterAltMode, EditState.Alt) {}
                edge(EditEvent.EnterCommandMode, EditState.Command) {}
            }
            state(EditState.Command) {
                edge(EditEvent.ExitCommandMode, EditState.Normal) {}
                edge(EditEvent.EnterDialogMode, EditState.Normal) {}
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

    private val stage by lazy {
        val aStage = Stage(ExtendViewport(1600f, 1200f, OrthographicCamera()), batch)
        aStage.actors {
            val vg = horizontalGroup {
                setFillParent(true)
                top()
                right()
                verticalGroup {
                    left()
                    boundLabel({ "Map: $fileName ${if (isDirty) "*" else ""}" })
                    boundLabel({ currentControlMap.name })
                    boundLabel({ currentControlMap.toString() })
                }
            }
        }
        aStage
    }

    override val camera = OrthographicCamera().apply {
        setToOrtho(false)
    }
    override val viewport = FitViewport(32f, 32f, camera)
    val shapeDrawer by lazy { Assets.shapeDrawer }
    private val gridSz = 33f
    private val squarSz = gridSz - 1f
    private val testCoordinate = Coordinate(0, 0)
    private val minX get() = gridMap.keys.minOf { it.x }
    private val minY get() = gridMap.keys.minOf { it.y }
    private val maxX get() = gridMap.keys.maxOf { it.x }
    private val maxY get() = gridMap.keys.maxOf { it.y }
    private val cameraMove = vec2()
    private val maxWidth = 60
    private val maxHeight = 60

    private var currentControlMap: CommandMap = normalCommandMap
    private var cursorX = 0
    private var cursorY = 0
    private var blinkTime = 0f
    private var blink = false
    private val blinkOn = Color(1f, 1f, 1f, 0.5f)
    private val blinkOff = Color(0f, 1f, 0f, 0.25f)
    private val zoomFactor = 0.05f
    private var cameraZoom: Float = 0f
    private var gridMap: MutableMap<Coordinate, SectionDefinition> =
        mutableMapOf(Coordinate(0, 0) to SectionDefinition.Start)
    private var sectionToSet: SectionDefinition = SectionDefinition.Corridor

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
        stage.act()
        stage.draw()
    }

    override fun keyDown(keycode: Int): Boolean {
        return currentControlMap.execute(keycode, KeyPress.Down)
    }

    private var mapIndex = 0
    private fun saveMap() {
        isDirty = false
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
        for (x in minX..maxX) {
            mapAsString += "-"
        }
        mapAsString += "\n"
        mapAsString += """name
$fileName            
start
Replace this with a start-of-level
message to inspire the players
success
Shows this when players win the level
fail
This is when they fail
facts
MaxEnemies:10:i
MaxSpawnedEnemies:20:i
AcceleratingSpawns:false:b
AcceleratingSpawnsFactor:1.25:f
EnemyKillCount:0:i
TargetEnemyKillCount:0:i
ShowEnemyKillCount:false:b

stories
start-story
level-failed
level-complete
basic-story            
        """.trimIndent()
        val potentialFileName = "text_maps/$fileName.txt"
        val handle = Gdx.files.local(potentialFileName)
//        if(handle.exists()) {
//            var lookingForNewName = true
//            while(lookingForNewName) {
//                mapIndex++
//                potentialFileName = "text_maps/new_map_$mapIndex.txt"
//                handle = Gdx.files.local(potentialFileName)
//                lookingForNewName = handle.exists()
//            }
//        }
        handle.writeString(mapAsString, false)
    }

    private fun delete() {
        val coordinate = Coordinate(cursorX, cursorY)
        if (gridMap.count() > 1 && gridMap.containsKey(coordinate)) {
            gridMap.remove(Coordinate(cursorX, cursorY))
            isDirty = true
        }
    }

    private fun insert(sectionType: SectionDefinition) {
        val coordinate = Coordinate(cursorX, cursorY)
        if (!gridMap.containsKey(coordinate) || gridMap[coordinate] != sectionType) {
            gridMap[coordinate] = sectionType
            isDirty = true
        }
    }

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

fun boundMoveTo(xFunc: () -> Float, yFunc: () -> Float): BoundMoveToAction {
    return BoundMoveToAction(xFunc, yFunc)
}

class BoundMoveToAction(val xFunc: () -> Float, val yFunc: () -> Float) : MoveToAction() {
    override fun act(delta: Float): Boolean {
        x = xFunc()
        y = yFunc()
        return super.act(delta)
    }
}