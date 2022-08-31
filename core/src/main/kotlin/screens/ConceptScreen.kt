package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.plus
import ktx.math.vec2
import ktx.math.vec3
import screens.stuff.Bone3d
import screens.stuff.toIso
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.Assets

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    override val viewport = ExtendViewport(200f, 180f)

    var zoom = 0f
    var rotationY = 0f
    var rotationX = 0f
    var elapsedTime = 0f
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    private val skeleton = Bone3d("body", vec3(), vec3(0f, 10f, 0f)).apply {
        addChild(Bone3d("left-arm-upper", vec3(-2.5f, 0f, 0f), vec3(0f, 5f, 0f)).apply {
            addChild(Bone3d("left-arm-lower", vec3(0f, 0f, 0f), vec3(0f, 5f, 0f)))
        })
        addChild(Bone3d("right-arm-upper", vec3(2.5f, 0f, 0f), vec3(0f, 5f, 0f)).apply {
            addChild(Bone3d("right-arm-lower", vec3(0f, 0f, 0f), vec3(0f, 5f, 0f)))
        })
        addChild(Bone3d("right-leg", vec3(3f, -10f, 0f), vec3(0f, -10f, 0f)))
        addChild(Bone3d("left-leg", vec3(-3f, -10f, 0f), vec3(0f, -10f, 0f)))
    }

    override fun render(delta: Float) {
        elapsedTime += delta
        updateMouse()
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
        super.render(delta)



        for (value in skeleton.bones.filterKeys { it.contains("body") }.values) {
            value.rotateBy(rotationY, Vector3.Y)
        }
        for (value in skeleton.bones.filterKeys { it.contains("arm-upper") }.values) {
            value.rotateBy(rotationX, Vector3.X)
        }

        skeleton.update(delta)
        batch.use {
            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)
            drawSkeletonRecursive(skeleton)
        }
    }

    private fun drawSkeletonRecursive(bone3d: Bone3d) {
        val offset = vec2(-100f)
        for (child in bone3d.children)
            drawSkeletonRecursive(child)
        shapeDrawer.filledCircle(bone3d.globalStart.toIso() + offset, 1f, Color.GREEN)
        shapeDrawer.line(bone3d.globalStart.toIso() + offset, bone3d.globalEnd.toIso() + offset, 1f)
        shapeDrawer.filledCircle(bone3d.globalEnd.toIso() + offset, 1f, Color.RED)

        offset.x += 50f
        val start2d = vec2(bone3d.globalStart.x, bone3d.globalStart.y)
        val end2d = vec2(bone3d.globalEnd.x, bone3d.globalEnd.y)
        shapeDrawer.filledCircle(start2d + offset, 1f, Color.GREEN)
        shapeDrawer.line(start2d + offset, end2d + offset, 1f)
        shapeDrawer.filledCircle(end2d + offset, 1f, Color.RED)
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
        setBoth(Input.Keys.A, "Rotate Left", { rotationY = 0f }) { rotationY =5f }
        setBoth(Input.Keys.D, "Rotate Right", { rotationY = 0f }) { rotationY = -5f }
        setBoth(Input.Keys.W, "Rotate X", { rotationX = 0f }) { rotationX = 5f }
        setBoth(Input.Keys.S, "Rotate X", { rotationX = 0f }) { rotationX = -5f }
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
