package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.plus
import ktx.math.unaryMinus
import ktx.math.vec2
import ktx.math.vec3
import screens.stuff.Bone3d
import screens.stuff.toIso
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.Assets

open class DirectionThingie {
    val forward = vec3(0f, 0f, -25f) //towards screen
    val up = vec3(0f, 25f, 0f)
    val thePerp: Vector3 get() { return forward.cpy().crs(up).nor().scl(25f) }
    val reversePerp: Vector3 get() { return -thePerp }

    val position = vec3()

    

    fun rotate(aroundUp: Float, aroundThePerp: Float, aroundForward:Float) {
        val q = Quaternion(up.cpy().nor(), aroundUp).mul(Quaternion(thePerp.cpy().nor(), aroundThePerp)).mul(Quaternion(forward.cpy().nor(), aroundForward))
        q.transform(forward)
        q.transform(up)
    }
}

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    override val viewport = ExtendViewport(200f, 180f)

    var zoom = 0f
    var rotationY = 0f
    var rotationX = 0f
    var rotationZ = 0f
    var elapsedTime = 0f
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    val t = DirectionThingie()

    override fun render(delta: Float) {
        elapsedTime += delta
        updateMouse()
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
        super.render(delta)

//        boneList.selectedItem.rotateAround(rotationY, rotationX, rotationZ)
//        boneList.selectedItem.rotateBy(rotationY, Vector3.Y)
//        boneList.selectedItem.rotateBy(rotationX, Vector3.X)
//        boneList.selectedItem.rotateBy(rotationZ, Vector3.Z)


//        for (value in skeleton.bones.filterKeys { it.contains("body") }.values) {
//            value.rotateBy(rotationY, Vector3.Y)
//        }
//        for (value in skeleton.bones.filterKeys { it.contains("arm-upper") }.values) {
//            value.rotateBy(rotationX, Vector3.X)
//        }

        t.rotate(rotationY, rotationX, rotationZ)
        batch.use {
            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)

            drawThing()
//            drawSkeletonRecursive(skeleton)
        }
    }

    private fun drawThing() {
        val position = vec2(-100f)
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(position, position + t.forward.toIso(), .5f)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(position, position + t.up.toIso(), .5f)
        shapeDrawer.setColor(Color.GREEN)
        shapeDrawer.line(position, position + t.thePerp.toIso(), .5f)
        shapeDrawer.setColor(Color.WHITE)
        shapeDrawer.filledCircle(position, 1f, Color.WHITE)
        shapeDrawer.filledCircle(position + t.forward.toIso(), 1f, Color.BLUE)
        shapeDrawer.filledCircle(position + t.up.toIso(), 1f, Color.RED)
        shapeDrawer.filledCircle(position + t.thePerp.toIso(), 1f, Color.GREEN)

        position.x += 50f
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(position.x, position.y, position.x + t.forward.x, position.y + t.forward.y, .5f)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(position.x, position.y, position.x + t.up.x, position.y + t.up.y, .5f)
        shapeDrawer.setColor(Color.GREEN)
        shapeDrawer.line(position.x, position.y, position.x + t.thePerp.x, position.y + t.thePerp.y, .5f)
        shapeDrawer.setColor(Color.WHITE)
        shapeDrawer.filledCircle(position, 1f, Color.WHITE)
        shapeDrawer.filledCircle(position.x + t.forward.x, position.y + t.forward.y, 1f, Color.BLUE)
        shapeDrawer.filledCircle(position.x + t.up.x, position.y + t.up.y, 1f, Color.RED)
        shapeDrawer.filledCircle(position.x + t.thePerp.x, position.y + t.thePerp.y, 1f, Color.GREEN)

        position.x += 50f
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(position.x, position.y, position.x + t.forward.y, position.y + t.forward.z, .5f)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(position.x, position.y, position.x + t.up.y, position.y + t.up.z, .5f)
        shapeDrawer.setColor(Color.GREEN)
        shapeDrawer.line(position.x, position.y, position.x + t.thePerp.y, position.y + t.thePerp.z, .5f)
        shapeDrawer.setColor(Color.WHITE)
        shapeDrawer.filledCircle(position, 1f, Color.WHITE)
        shapeDrawer.filledCircle(position.x + t.forward.y, position.y + t.forward.z, 1f, Color.BLUE)
        shapeDrawer.filledCircle(position.x + t.up.y, position.y + t.up.z, 1f, Color.RED)
        shapeDrawer.filledCircle(position.x + t.thePerp.y, position.y + t.thePerp.z, 1f, Color.GREEN)

        position.x += 50f
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(position.x, position.y, position.x + t.forward.z, position.y + t.forward.x, .5f)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(position.x, position.y, position.x + t.up.z, position.y + t.up.x, .5f)
        shapeDrawer.setColor(Color.GREEN)
        shapeDrawer.line(position.x, position.y, position.x + t.thePerp.z, position.y + t.thePerp.x, .5f)
        shapeDrawer.setColor(Color.WHITE)
        shapeDrawer.filledCircle(position, 1f, Color.WHITE)
        shapeDrawer.filledCircle(position.x + t.forward.z, position.y + t.forward.x, 1f, Color.BLUE)
        shapeDrawer.filledCircle(position.x + t.up.z, position.y + t.up.x, 1f, Color.RED)
        shapeDrawer.filledCircle(position.x + t.thePerp.z, position.y + t.thePerp.x, 1f, Color.GREEN)
    }

    private fun drawSkeletonRecursive(bone3d: Bone3d) {
        val offset = vec2(-100f)
        for (child in bone3d.children)
            drawSkeletonRecursive(child)
        shapeDrawer.filledCircle(bone3d.globalStart.toIso() + offset, 1f, Color.GREEN)
        shapeDrawer.line(bone3d.globalStart.toIso() + offset, bone3d.globalEnd.toIso() + offset, .5f)
        shapeDrawer.filledCircle(bone3d.globalEnd.toIso() + offset, .5f, Color.RED)

        offset.x += 50f
        val start2d = vec2(bone3d.globalStart.x, bone3d.globalStart.y)
        val end2d = vec2(bone3d.globalEnd.x, bone3d.globalEnd.y)
        shapeDrawer.filledCircle(start2d + offset, 1f, Color.GREEN)
        shapeDrawer.line(start2d + offset, end2d + offset, 0.5f)
        shapeDrawer.filledCircle(end2d + offset, .5f, Color.RED)
        offset.x += 50f
        start2d.set(bone3d.globalStart.y, bone3d.globalStart.z)
        end2d.set(bone3d.globalEnd.y, bone3d.globalEnd.z)
        shapeDrawer.filledCircle(start2d + offset, 1f, Color.GREEN)
        shapeDrawer.line(start2d + offset, end2d + offset, 1f)
        shapeDrawer.filledCircle(end2d + offset, 1f, Color.RED)
        offset.x += 50f
        start2d.set(bone3d.globalStart.z, bone3d.globalStart.x)
        end2d.set(bone3d.globalEnd.z, bone3d.globalEnd.x)
        shapeDrawer.filledCircle(start2d + offset, 1f, Color.GREEN)
        shapeDrawer.line(start2d + offset, end2d + offset, 1f)
        shapeDrawer.filledCircle(end2d + offset, 1f, Color.RED)
    }

    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.Z, "Zoom in", { zoom = 0f }, { zoom = 0.1f })
        setBoth(Input.Keys.X, "Zoom out", { zoom = 0f }, { zoom = -0.1f })
//        setUp(Input.Keys.LEFT, "Previous bone") {
//            boneList.previousItem()
//            info { "Current bone: ${boneList.selectedItem.name}" }
//        }
//        setUp(Input.Keys.RIGHT, "Next bone") {
//            boneList.nextItem()
//            info { "Current bone: ${boneList.selectedItem.name}" }
//        }
        setBoth(Input.Keys.A, "Rotate Left", { rotationY = 0f }) { rotationY = 5f }
        setBoth(Input.Keys.D, "Rotate Right", { rotationY = 0f }) { rotationY = -5f }
        setBoth(Input.Keys.W, "Rotate X", { rotationX = 0f }) { rotationX = 5f }
        setBoth(Input.Keys.S, "Rotate X", { rotationX = 0f }) { rotationX = -5f }
        setBoth(Input.Keys.UP, "Rotate Z", { rotationZ = 0f }) { rotationZ = -5f }
        setBoth(Input.Keys.DOWN, "Rotate Z", { rotationZ = 0f }) { rotationZ = -5f }
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
