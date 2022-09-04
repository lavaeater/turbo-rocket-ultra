package screens.concepts

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import eater.core.selectedItemListOf
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.log.info
import ktx.math.vec2
import ktx.math.vec3
import screens.BasicScreen
import screens.command
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.Assets



class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    override val viewport = ExtendViewport(200f, 180f)

    var zoom = 0f
    var rotRight = 0f
    var rotUp = 0f
    var rotForward = 0f
    var rotY = 0f
    var rotX = 0f
    var rotZ = 0f
    var modifier = 1f
    var elapsedTime = 0f
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    val node = Node("base", vec3(), 0f, 0f, 0f).apply {
        addChild(Node("other thing", vec3(100f,0f,0f), 15f, 15f, 15f).apply {
            addChild(Segment("a segmebt", vec3(0f, 50f, 0f),100f, Direction3d(25f, 0f,0f),Color.WHITE))
        })
    }

    val thingList = selectedItemListOf(*node.flatChildren.values.toTypedArray())

    override fun render(delta: Float) {
        elapsedTime += delta
        updateMouse()
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
        super.render(delta)

        if (!MathUtils.isZero(rotUp))
            thingList.selectedItem.rotateAroundUp(rotUp)

        if (!MathUtils.isZero(rotRight))
            thingList.selectedItem.rotateAroundRight(rotRight)

        if (!MathUtils.isZero(rotForward))
            thingList.selectedItem.rotateAroundForward(rotForward)

        if (!MathUtils.isZero(rotX))
            thingList.selectedItem.rotateAroundX(rotX)
        if (!MathUtils.isZero(rotY))
            thingList.selectedItem.rotateAroundY(rotY)
        if (!MathUtils.isZero(rotZ))
            thingList.selectedItem.rotateAroundZ(rotZ)

        batch.use {
            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)
            node.renderIso(shapeDrawer)

        }
    }


    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.Z, "Zoom in", { zoom = 0f }, { zoom = 0.1f })
        setBoth(Input.Keys.X, "Zoom out", { zoom = 0f }, { zoom = -0.1f })
        setUp(Input.Keys.LEFT, "Previous bone") {
            thingList.previousItem()
            info { "Current bone: ${thingList.selectedItem.name}" }
        }
        setUp(Input.Keys.RIGHT, "Next bone") {
            thingList.nextItem()
            info { "Current bone: ${thingList.selectedItem.name}" }
        }
        setBoth(Input.Keys.A, "Rotate Left", { rotRight = 0f }) { rotRight = 5f * modifier }
        setBoth(Input.Keys.D, "Rotate Right", { rotX = 0f }) { rotX = 5f * modifier }
        setBoth(Input.Keys.W, "Rotate X", { rotUp = 0f }) { rotUp = 5f * modifier }
        setBoth(Input.Keys.S, "Rotate X", { rotY = 0f }) { rotY = 5f * modifier }
        setBoth(Input.Keys.Q, "Rotate Z", { rotForward = 0f }) { rotForward = 5f * modifier }
        setBoth(Input.Keys.E, "Rotate Z", { rotZ = 0f }) { rotZ = 5f * modifier }
        setBoth(Input.Keys.SHIFT_RIGHT, "inverse input", { modifier = 1f}) { modifier = -1f}
    }


    override fun keyUp(keycode: Int): Boolean {
        return normalCommandMap.execute(keycode, KeyPress.Up)
    }

    override fun keyDown(keycode: Int): Boolean {
        return normalCommandMap.execute(keycode, KeyPress.Down)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Buttons.LEFT) {
        }
        return true
    }

    val screenMouse = vec3()
    val mousePosition = vec2()
    fun updateMouse() {
        screenMouse.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(screenMouse)
        mousePosition.set(screenMouse.x, screenMouse.y)
    }
}

/*

        position.x += 50f
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(position.x + thing.position.x, position.y, position.x + thing.position.x + thing.forward.x, position.y + thing.position.y + thing.forward.y, .5f)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(position.x + thing.position.x, position.y, position.x + thing.position.x + thing.up.x, position.y + thing.position.y + thing.up.y, .5f)
        shapeDrawer.setColor(Color.GREEN)
        shapeDrawer.line(position.x + thing.position.x, position.y, position.x + thing.position.x + thing.thePerp.x, position.y + thing.position.y + thing.thePerp.y, .5f)
        shapeDrawer.setColor(Color.WHITE)
        shapeDrawer.filledCircle(position.x + thing.position.x, position.y + thing.position.y, 1f, Color.WHITE)
        shapeDrawer.filledCircle(position.x + thing.position.x + thing.forward.x, position.y + thing.position.y + thing.forward.y, 1f, Color.BLUE)
        shapeDrawer.filledCircle(position.x + thing.position.x + thing.up.x, position.y + thing.position.y + thing.up.y, 1f, Color.RED)
        shapeDrawer.filledCircle(position.x + thing.position.x + thing.thePerp.x, position.y + thing.position.y + thing.thePerp.y, 1f, Color.GREEN)

        position.x += 50f
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(position.x + thing.position.y, position.y + thing.position.z, position.x + thing.position.y + thing.forward.y, position.y + thing.position.z + thing.forward.z, .5f)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(position.x + thing.position.y, position.y + thing.position.z, position.x + thing.position.y + thing.up.y, position.y + thing.position.z + thing.up.z, .5f)
        shapeDrawer.setColor(Color.GREEN)
        shapeDrawer.line(position.x + thing.position.y, position.y + thing.position.z, position.x + thing.position.y + thing.thePerp.y, position.y + thing.position.z + thing.thePerp.z, .5f)
        shapeDrawer.setColor(Color.WHITE)
        shapeDrawer.filledCircle(position.x + thing.position.y, position.y + thing.position.z, 1f, Color.WHITE)
        shapeDrawer.filledCircle(position.x + thing.position.y + thing.forward.y, position.y + thing.position.z + thing.forward.z, 1f, Color.BLUE)
        shapeDrawer.filledCircle(position.x + thing.position.y + thing.up.y, position.y + thing.position.z + thing.up.z, 1f, Color.RED)
        shapeDrawer.filledCircle(position.x + thing.position.y + thing.thePerp.y, position.y + thing.position.z + thing.thePerp.z, 1f, Color.GREEN)

        position.x += 50f
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(position.x, position.y, position.x + thing.position.z + thing.forward.z, position.y + thing.position.x + thing.forward.x, .5f)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(position.x, position.y, position.x + thing.position.z + thing.up.z, position.y + thing.position.x + thing.up.x, .5f)
        shapeDrawer.setColor(Color.GREEN)
        shapeDrawer.line(position.x, position.y, position.x + thing.position.z + thing.thePerp.z, position.y + thing.position.x + thing.thePerp.x, .5f)
        shapeDrawer.setColor(Color.WHITE)
        shapeDrawer.filledCircle(position.x + thing.position.z, position.x + thing.position.x, 1f, Color.WHITE)
        shapeDrawer.filledCircle(position.x + thing.position.z + thing.forward.z, position.y + thing.position.x + thing.forward.x, 1f, Color.BLUE)
        shapeDrawer.filledCircle(position.x + thing.position.z + thing.up.z, position.y + thing.position.x + thing.up.x, 1f, Color.RED)
        shapeDrawer.filledCircle(position.x + thing.position.z + thing.thePerp.z, position.y + thing.position.x + thing.thePerp.x, 1f, Color.GREEN)
 */
