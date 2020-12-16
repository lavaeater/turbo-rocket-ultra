package tru

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.*
import space.earlygrey.shapedrawer.ShapeDrawer
import com.badlogic.gdx.graphics.g2d.TextureRegion

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils.*
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import control.InputManager
import control.ShipControl
import ktx.box2d.*
import ktx.graphics.use
import ktx.math.vec2

const val GAMEWIDTH = 100f
const val GAMEHEIGHT = 75f


/** First screen of the application. Displayed after the application is created.  */
class FirstScreen : Screen {

    companion object {
        private const val MAX_STEP_TIME = 1 / 300f
        private var accumulator = 0f
        private val ROF = 1f / 10f
    }

    private var lastShot = 0f
    private val shapeTexture: TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(1, 1)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()
        TextureRegion(texture, 1, 1)
    }

    private val control = ShipControl()
    private val batch = SpriteBatch()
    private val shapeDrawer = ShapeDrawer(batch, shapeTexture)
    private val world = createWorld(vec2(0f, 10f))
    private val camera = OrthographicCamera()
    private val viewPort = ExtendViewport(GAMEWIDTH, GAMEHEIGHT, camera)

    private val box2DDebugRenderer = Box2DDebugRenderer()
    private var needsInit = true
    private lateinit var ship: Body

    private lateinit var box: Body

    override fun show() {
        if (needsInit) {
            setupInput()
            createBodies()
            camera.setToOrtho(true, viewPort.maxWorldWidth, viewPort.maxWorldHeight)
            needsInit = false
        }
    }

    private fun setupInput() {
        Gdx.input.inputProcessor = InputManager(control)
    }

    private fun handleShooting(delta: Float) {
        if (control.firing) {
            lastShot += delta
            if (lastShot > ROF) {
                lastShot = 0f
                val positionVector = vec2(cos(ship.angle), sin(ship.angle)).rotate90(1).scl(2f)
                val shot = world.body {
                    type = BodyDef.BodyType.DynamicBody
                    circle(position = ship.worldCenter.cpy().add(positionVector), radius = .2f) {}
                }
                shot.linearVelocity =positionVector.scl(1000f)
//                val forceVector = vec2(cos(ship.angle), sin(ship.angle)).rotate90(1)
//                shot.applyLinearImpulse(forceVector.scl(10f), ship.worldCenter, true)
            }
        }
    }

    private fun createBodies() {
        ship = world.body {
            type = BodyDef.BodyType.DynamicBody
            polygon(Vector2(-1f, -1f), Vector2(0f, 1f), Vector2(1f, -1f)) {
                density = 1f
            }
            linearDamping = 1f
            angularDamping = 5f
        }

        for (x in 0..99)
            for (y in 0..99) {
                world.body {
                    type = BodyDef.BodyType.StaticBody
                    box(2f, 2f, vec2(x * 25f, y * 25f))
                }
            }

        box = world.body {
            type = BodyDef.BodyType.StaticBody
            box(300f, 2f, vec2(0f, 30f))
        }
    }

    private fun updateBox2D(delta: Float) {
        val frameTime = delta.coerceAtMost(0.25f)
        accumulator += frameTime
        if (accumulator >= MAX_STEP_TIME) {
            world.step(delta, 6, 2)
            accumulator -= MAX_STEP_TIME
        }
    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(.3f, .5f, .8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput(delta)
        updateBox2D(delta)
        updateCamera()
        batch.projectionMatrix = camera.combined
        batch.use {
        }
        box2DDebugRenderer.render(world, camera.combined)
    }

    private fun updateCamera() {
        camera.position.set(ship.position.x, ship.position.y, 0f)
        camera.update(true)
    }

    private fun handleInput(delta: Float) {
        /*
        To make things easy, we have only one control and one
        ship - easy
         */
        handleShooting(delta)
        if (control.rotation != 0f) {
            ship.applyTorque(20f * control.rotation, true)
        }

        val forceVector = vec2(cos(ship.angle), sin(ship.angle)).rotate90(1)

        if (control.thrust > 0f)
            ship.applyForceToCenter(forceVector.scl(100f), true)
    }

    override fun resize(width: Int, height: Int) {
        viewPort.update(width, height)
        batch.projectionMatrix = camera.combined
    }

    override fun pause() {
        // Invoked when your application is paused.
    }

    override fun resume() {
        // Invoked when your application is resumed after pause.
    }

    override fun hide() {
        // This method is called when another screen replaces this one.
    }

    override fun dispose() {
        // Destroy screen's assets here.
    }
}