package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import data.selectedItemListOf
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.log.info
import ktx.math.plus
import ktx.math.vec2
import ktx.math.vec3
import screens.stuff.Bone3d
import screens.stuff.selectRecursive
import screens.stuff.toIso
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.Assets

fun IThing.toMap(): Map<String, IThing> {
    return listOf(
        this,
        *children.asSequence().selectRecursive { children.asSequence() }.toList().toTypedArray()
    ).associateBy { it.name }
}

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    override val viewport = ExtendViewport(200f, 180f)

    var zoom = 0f
    var aroundUp = 0f
    var aroundLeft = 0f
    var aroundForward = 0f
    var elapsedTime = 0f
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    val t = Bone("body", vec3(), 20f).apply {
        rotate(0f, -90f, 0f)
        addChild(Bone("left-arm-upper", vec3(10f, 0f, 0f), 10f).apply {
            rotateAroundUpEnabled = false
            addChild(Bone("left-arm-lower", vec3(0f, 0f, -10f), 10f).apply {
                rotateAroundForwardEnabled = false
                rotateAroundUpEnabled = false
            })
        })
        addChild(Bone("right-arm-upper", vec3(-10f, 0f, 0f), 10f).apply {
            rotateAroundUpEnabled = false
            addChild(Bone("right-arm-lower", vec3(0f, 0f, -10f), 10f).apply {
                rotateAroundForwardEnabled = false
                rotateAroundUpEnabled = false
            })
        })
    }

    val thingList = selectedItemListOf(*t.allThings.values.toTypedArray())

    override fun render(delta: Float) {
        elapsedTime += delta
        updateMouse()
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
        super.render(delta)

        if(!MathUtils.isZero(aroundUp) || !MathUtils.isZero(aroundLeft) || !MathUtils.isZero(aroundForward))
            thingList.selectedItem.rotate(aroundUp, aroundLeft, aroundForward)
        batch.use {
            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)

            drawThingRecursive(t)
//            drawSkeletonRecursive(skeleton)
        }
    }

    private fun drawThingRecursive(thing: IThing) {
        for (child in thing.children)
            drawThingRecursive(child)

        if (thing is Bone)
            drawBone(thing)
        else {
            val position = vec2(-100f)
            shapeDrawer.setColor(Color.BLUE)
            shapeDrawer.line(
                position + thing.position.toIso(),
                position + thing.position.toIso() + thing.forward.toIso(),
                .5f
            )
            shapeDrawer.setColor(Color.RED)
            shapeDrawer.line(
                position + thing.position.toIso(),
                position + thing.position.toIso() + thing.up.toIso(),
                .5f
            )
            shapeDrawer.setColor(Color.GREEN)
            shapeDrawer.line(
                position + thing.position.toIso(),
                position + thing.position.toIso() + thing.leftOrRight.toIso(),
                .5f
            )
            shapeDrawer.setColor(Color.WHITE)
            shapeDrawer.filledCircle(position + thing.position.toIso(), 1f, Color.WHITE)
            shapeDrawer.filledCircle(position + thing.position.toIso() + thing.forward.toIso(), 1f, Color.BLUE)
            shapeDrawer.filledCircle(position + thing.position.toIso() + thing.up.toIso(), 1f, Color.RED)
            shapeDrawer.filledCircle(position + thing.position.toIso() + thing.leftOrRight.toIso(), 1f, Color.GREEN)
        }
    }

    private fun drawBone(bone: Bone) {
        //drawOrientation(bone.position, bone.orientation)
        shapeDrawer.setColor(Color.GRAY)
        shapeDrawer.line(bone.position.toIso(), bone.boneEnd.toIso())
        shapeDrawer.filledCircle(bone.position.toIso(), 1f, Color.GREEN)
        shapeDrawer.filledCircle(bone.boneEnd.toIso(), 1f, Color.BLUE)
    }

    private fun drawOrientation(position: Vector3, orientation: Orientation) {
        val up = orientation.up.cpy().scl(10f).toIso()
        val forward = orientation.forward.cpy().scl(10f).toIso()
        val left = orientation.leftOrRight.cpy().scl(10f).toIso()
        val isoPos = position.toIso()
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(isoPos, isoPos + forward, .5f)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(isoPos, isoPos + up, .5f)
        shapeDrawer.setColor(Color.GREEN)
        shapeDrawer.line(isoPos, isoPos + left, .5f)
        shapeDrawer.setColor(Color.WHITE)
        shapeDrawer.filledCircle(isoPos, 1f, Color.WHITE)
        shapeDrawer.filledCircle(isoPos + forward, 1f, Color.BLUE)
        shapeDrawer.filledCircle(isoPos + up, 1f, Color.RED)
        shapeDrawer.filledCircle(isoPos + left, 1f, Color.GREEN)
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
        setUp(Input.Keys.LEFT, "Previous bone") {
            thingList.previousItem()
            info { "Current bone: ${thingList.selectedItem.name}" }
        }
        setUp(Input.Keys.RIGHT, "Next bone") {
            thingList.nextItem()
            info { "Current bone: ${thingList.selectedItem.name}" }
        }
        setBoth(Input.Keys.A, "Rotate Left", { aroundUp = 0f }) { aroundUp = 5f }
        setBoth(Input.Keys.D, "Rotate Right", { aroundUp = 0f }) { aroundUp = -5f }
        setBoth(Input.Keys.W, "Rotate X", { aroundLeft = 0f }) { aroundLeft = 5f }
        setBoth(Input.Keys.S, "Rotate X", { aroundLeft = 0f }) { aroundLeft = -5f }
        setBoth(Input.Keys.Q, "Rotate Z", { aroundForward = 0f }) { aroundForward = -5f }
        setBoth(Input.Keys.E, "Rotate Z", { aroundForward = 0f }) { aroundForward = 5f }
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
