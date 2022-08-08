package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.TextureRegion
import gamestate.GameEvent
import gamestate.GameState
import graphics.ContainerGeometry
import graphics.GeometryLine
import isometric.toIsometric
import ktx.collections.GdxArray
import ktx.graphics.use
import ktx.math.minus
import ktx.math.vec2
import ktx.math.vec3
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.*

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

    val scoutTexture = Texture(Gdx.files.internal("sprites/scout/scout.png"))
    val scoutNE = Animation<TextureRegion>(
        0.1f, GdxArray(Array(5) {
            TextureRegion(scoutTexture, it * 75, 0, 75, 75)
        }), PlayMode.LOOP
    )

    /**
     * What we want to do is basically what we did for anchor points, but perhaps
     * with some tweaking?
     *
     * We want two points on the end of a line. They are, like everything else, always
     * projected into an isometric space.
     *
     * So, the points are equidistant from a center point, which is a point, 100,100
     */

    val centerPoint = vec2(0f, 0f)
//    val line = Line(vec2(12.5f, 0f), vec2(-12.5f, 0f))

    /**
     * Now I think I have figured out another thing that I kind of want.
     *
     * The arms need not change length or direction (useful in and of itself, of course),
     * but rather I want to say that attached to a line's endpoint is the startpoint
     * of another line, relating to the first's geometry somehow, perhaps using a
     * related angle or something. A rotation. A hierarchy of points that describe
     * lines that relate to each other.
     *
     * We are truly and deeply into the weeds, but push on, my friend.
     */

    val baseGeometry: ContainerGeometry by lazy {
        val shoulderLine = GeometryLine(vec2(), 15f, 45f)
        val rightArmLine = GeometryLine(shoulderLine.e1, 45f, 45f)
        shoulderLine.add(rightArmLine)
        val cGeom = ContainerGeometry(vec2(), 0f).apply {
            add(shoulderLine)
        }
        cGeom
    }
    var zoom = 0f
    var rotation = 0f
    var extension = 0f
//    val triangle = Triangle(vec2(), 7.5f, 15f, 15f, 165f)


    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.Z, "Zoom in", { zoom = 0f }, { zoom = 1.0f })
        setBoth(Input.Keys.X, "Zoom out", { zoom = 0f }, { zoom = -1.0f })
        setBoth(Input.Keys.A, "Rotate Left", { rotation = 0f }) { rotation = -1.0f }
        setBoth(Input.Keys.D, "Rotate Right", { rotation = 0f }) { rotation = 1.0f }
        setBoth(Input.Keys.W, "Extend", { extension = 0f }) { extension = .1f }
        setBoth(Input.Keys.S, "Reverse", { extension = 0f }) { extension = -.1f }
    }
    private val shapeDrawer by lazy { Assets.shapeDrawer }

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
        return true
    }

    val screenMouse = vec3()
    val mousePosition = vec2()
    val mouseToCenter = vec2()
    fun updateMouse() {
        screenMouse.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(screenMouse)
        mousePosition.set(screenMouse.x, screenMouse.y)
        mouseToCenter.set(mousePosition - baseGeometry.worldPosition)
        baseGeometry.worldRotation = mouseToCenter.angleDeg() - 90f
//        line.rotation = mouseToCenter.angleDeg() - 135f
//        triangle.updateInverseKinematic(mousePosition)
        //triangle.rotation = mouseToCenter.angleDeg()
    }

    var elapsedTime = 0f
    val scoutPosition = vec2()
    override fun render(delta: Float) {
        elapsedTime += delta
        updateMouse()
        baseGeometry.updateGeometry()
//        triangle.updateA(triangle.a + extension)
//        triangle.position.set(line.e1.toMutable())
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
//        line.rotation = line.rotation + rotation
        super.render(delta)
        batch.use {
//            shapeDrawer.line(line.e1.toMutable(), line.e2.toMutable(), 1f)
//            shapeDrawer.filledCircle(line.e1.toMutable(), 2.5f, Color.GREEN)
//            shapeDrawer.filledCircle(line.e2.toMutable(), 2.5f, Color.BLUE)
//            shapeDrawer.filledCircle(line.center.toMutable(), 1.5f, Color.RED)


            scoutPosition.set(baseGeometry.worldX - 75 / 2, baseGeometry.worldY - 75 / 2)
            batch.draw(scoutNE.getKeyFrame(elapsedTime), scoutPosition.x, scoutPosition.y)
            baseGeometry.draw(shapeDrawer)

            shapeDrawer.line(baseGeometry.worldPosition, mousePosition, 1f)


            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)
//            shapeDrawer.setColor(Color.YELLOW)
//            for (arm in triangle.arms) {
//                shapeDrawer.line(arm.first.toIsometric(), arm.second.toIsometric())
//            }

//            shapeDrawer.filledPolygon(triangle.polygonB)


        }
    }
}

