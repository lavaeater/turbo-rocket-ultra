package screens.concepts

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import data.selectedItemListOf
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.log.info
import ktx.math.minus
import ktx.math.plus
import ktx.math.vec2
import ktx.math.vec3
import screens.BasicScreen
import screens.command
import screens.stuff.AnimatedSprited3d
import screens.stuff.Bone3d
import screens.stuff.selectRecursive
import screens.stuff.toIso
import screens.ui.KeyPress
import space.earlygrey.shapedrawer.ShapeDrawer
import statemachine.StateMachine
import tru.Assets



class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    override val viewport = ExtendViewport(200f, 180f)

    var zoom = 0f
    var rotX = 0f
    var rotY = 0f
    var rotZ = 0f
    var elapsedTime = 0f
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    val node = Node("base", vec3(), 0f, 0f, 0f)

//    val thingList = selectedItemListOf(*t.allThings.values.toTypedArray())

    override fun render(delta: Float) {
        elapsedTime += delta
        updateMouse()
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
        super.render(delta)

//        if (!MathUtils.isZero(rotY))
//            thingList.selectedItem.rotateAgainstJoint(rotY)
//
//        if (!MathUtils.isZero(rotX))
//            thingList.selectedItem.rotateAroundParentForward(rotX)
//
//        if (!MathUtils.isZero(rotZ))
//            thingList.selectedItem.rotateAroundParentUp(rotZ)


        batch.use {
            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)
            node.renderIso(shapeDrawer)

        }
    }


    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.Z, "Zoom in", { zoom = 0f }, { zoom = 0.1f })
        setBoth(Input.Keys.X, "Zoom out", { zoom = 0f }, { zoom = -0.1f })
//        setUp(Input.Keys.LEFT, "Previous bone") {
//            thingList.previousItem()
//            info { "Current bone: ${thingList.selectedItem.name}" }
//        }
//        setUp(Input.Keys.RIGHT, "Next bone") {
//            thingList.nextItem()
//            info { "Current bone: ${thingList.selectedItem.name}" }
//        }
        setBoth(Input.Keys.A, "Rotate Left", { rotX = 0f }) { rotX = 5f }
        setBoth(Input.Keys.D, "Rotate Right", { rotX = 0f }) { rotX = -5f }
        setBoth(Input.Keys.W, "Rotate X", { rotY = 0f }) { rotY = 5f }
        setBoth(Input.Keys.S, "Rotate X", { rotY = 0f }) { rotY = -5f }
        setBoth(Input.Keys.Q, "Rotate Z", { rotZ = 0f }) { rotZ = -5f }
        setBoth(Input.Keys.E, "Rotate Z", { rotZ = 0f }) { rotZ = 5f }
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
