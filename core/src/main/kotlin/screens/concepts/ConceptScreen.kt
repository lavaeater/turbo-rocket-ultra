package screens.concepts

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import eater.core.SelectedItemList
import eater.core.selectedItemListOf
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.log.info
import ktx.math.vec2
import ktx.math.vec3
import screens.BasicScreen
import screens.command
import screens.stuff.toIso
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.Assets


class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    override val viewport = ExtendViewport(200f, 160f, 400f, 200f)

    init {
        camera.viewportHeight = 160f
        camera.viewportWidth = 200f
    }

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

    val character = getArm()

    val nodes = listOf(character)

    val rotateableNodes =
        selectedItemListOf(*character.flatChildren.filterValues { it.rotations.any() }.values.toTypedArray())

    val target3d = vec3()
    val targetChangeVector = vec3()

    fun pointArmAtMouse() {
        /*

        How do we do this the simplest way possible?

        IN inverse kinematics, we work from outside in,
        but I think I might want to work inside out, as an experiment.

        We have to iterate over all segments to turn then towards the target.

        I think we start outside in, first.

        So we need to find the "outermost"

        Nah, realistically, we shouldn't work with all segments, just
        really the ones that are on a "fixed" point on the model, i.e. one that can't
        turn itself - the shoulder for instance.

        So, we can say that the body should always try to FACE the mouse pointer, in 3d.

        lets just create a 3d point that we can move about in 3d space.

        Let's also start presenting some information on the screen, perhaps?


        So what is it everyone is saying about all this?

        I think we have to start with measuring the distance to the target.

        If the distance is less than the length of the segment chain - and yes, we
        should probably think about not having them be able to be in weird places, right?

        I think that's actually done, but I can code that in.

        So, we start at outer. If we are too far away, we will rotate all segments so they point towards
        target, outside in.

        If we are closer than, we will iterate over outside in with rotations to try and get there.

        Can we let the computer just do this like a complete retard?
         */

//        val leaf = (character.getNode("arm-lower") as Segment)
//
//        val dst2 = leaf.boneEnd.dst2(mousePosition3d)
        /**
         * Hey, cool thought: the arm can only rotate around some
         * axes - so how do we use that for IK?
         *
         * We can use it to calculate angles, for instance, against the
         *
         * x-z-plane
         */
        val dX = target3d.x - character.position.x
        val dY = target3d.z - character.position.z

        var theta = MathUtils.atan2(dX, dY) * MathUtils.radDeg
        if(theta < 0f)
            theta += 360f
        val forwardXZ = vec2(character.forward.z, character.forward.x)
        val currentTheta = forwardXZ.angleDeg()
        val degrees = theta - currentTheta
        info { "ct: $currentTheta" }
        info { "forward dot with Y or something: ${character.forward.cpy().dot(Vector3.X) * MathUtils.radDeg}"}
        character.rotate(RotationDirection.AroundY, degrees)
    }

    override fun render(delta: Float) {
        elapsedTime += delta
        updateMouse()
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
        super.render(delta)

        if (!MathUtils.isZero(rotUp))
            rotateableNodes.selectedItem.rotate(currentRotationList.selectedItem, rotUp)

        target3d.add(targetChangeVector)
        pointArmAtMouse()

        batch.use {
            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)
            nodes.forEach { it.renderIso(shapeDrawer) }
            shapeDrawer.filledCircle(target3d.toIso(), 0.5f, Color.GREEN)
        }
    }

    private val nodeRotationMap =
        character.flatChildren.values.associateWith { selectedItemListOf(*it.rotations.keys.toTypedArray()) }
    private val currentRotationList: SelectedItemList<RotationDirection>
        get() {
            return nodeRotationMap[rotateableNodes.selectedItem]!!
        }

    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.Z, "Zoom in", { zoom = 0f }, { zoom = 0.1f })
        setBoth(Input.Keys.X, "Zoom out", { zoom = 0f }, { zoom = -0.1f })
        setUp(Input.Keys.LEFT, "Previous bone") {
            rotateableNodes.previousItem()
            info { "Segment: ${rotateableNodes.selectedItem.name}" }
            info { "Rotation: ${currentRotationList.selectedItem}" }
        }
        setUp(Input.Keys.RIGHT, "Next bone") {
            rotateableNodes.nextItem()
            info { "Segment: ${rotateableNodes.selectedItem.name}" }
            info { "Rotation: ${currentRotationList.selectedItem}" }
        }
        setUp(Input.Keys.UP, "Previous bone") {
            currentRotationList.nextItem()
            info { "Rotation: ${currentRotationList.selectedItem}" }
        }
        setUp(Input.Keys.DOWN, "Next bone") {
            currentRotationList.previousItem()
            info { "Rotation: ${currentRotationList.selectedItem}" }
        }
        setBoth(Input.Keys.W, "Rotate Left", { rotRight = 0f }) { rotRight = 5f }
        setBoth(Input.Keys.S, "Rotate Right", { rotRight = 0f }) { rotRight = -5f }
        setBoth(Input.Keys.A, "Rotate X", { rotUp = 0f }) { rotUp = 5f }
        setBoth(Input.Keys.D, "Rotate X", { rotUp = 0f }) { rotUp = -5f }
        setBoth(Input.Keys.Q, "Rotate Z", { rotForward = 0f }) { rotForward = 5f }
        setBoth(Input.Keys.E, "Rotate Z", { rotForward = 0f }) { rotForward = -5f }
        setBoth(Input.Keys.NUMPAD_8, "Target UP", { targetChangeVector.y = 0f }) { targetChangeVector.y = -1f }
        setBoth(Input.Keys.NUMPAD_2, "Target DOWN", { targetChangeVector.y = 0f }) { targetChangeVector.y = 1f }
        setBoth(Input.Keys.NUMPAD_4, "Target LEFT", { targetChangeVector.x = 0f }) { targetChangeVector.x = -1f }
        setBoth(Input.Keys.NUMPAD_6, "Target RIGHT", { targetChangeVector.x = 0f }) { targetChangeVector.x = 1f }
        setBoth(Input.Keys.NUMPAD_7, "Target Z", { targetChangeVector.z = 0f }) { targetChangeVector.z = -1f }
        setBoth(Input.Keys.NUMPAD_1, "Target Z", { targetChangeVector.z = 0f }) { targetChangeVector.z = 1f }

        setUp(Input.Keys.SPACE, "Rotate Z") { character.reset() }
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
