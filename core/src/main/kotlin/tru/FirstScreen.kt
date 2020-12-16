package tru

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils.cos
import com.badlogic.gdx.math.MathUtils.sin
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.viewport.ExtendViewport
import control.InputManager
import control.ShipControl
import gamestate.Player
import ktx.box2d.*
import ktx.graphics.use
import ktx.math.random
import ktx.math.vec2
import physics.hasTag
import physics.bodyForTag
import physics.hasTags


class FirstScreen : Screen {

    companion object {
        private const val MAX_STEP_TIME = 1 / 300f
        private var accumulator = 0f
        private const val ROF = 1f / 10f
        const val TAG_SHIP = "SHIP"
        const val TAG_SHOT = "SHOT"
        const val TAG_BOX = "BOX"

        const val GAMEWIDTH = 100f
        const val GAMEHEIGHT = 75f
    }

    private var lastShot = 0f
    private val control = ShipControl()
    private val batch = SpriteBatch()
    private val world = createWorld(vec2(0f, 10f))
    private val camera = OrthographicCamera()
    private val viewPort = ExtendViewport(GAMEWIDTH, GAMEHEIGHT, camera)
    private val box2DDebugRenderer = Box2DDebugRenderer()
    private var needsInit = true
    private lateinit var ship: Body
    private val bodiesToDestroy = mutableListOf<Body>()
    private val player = Player()

    override fun show() {
        if (needsInit) {
            setupInput()
            setupBox2D()
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
                    userData = TAG_SHOT
                    circle(position = ship.worldCenter.cpy().add(positionVector), radius = .2f) {}
                }
                shot.linearVelocity = positionVector.scl(1000f)
            }
        }
    }

    private fun setupBox2D() {

        world.setContactListener(object : ContactListener {
            override fun beginContact(contact: Contact) {
                //Ship colliding with something
                if(contact.hasTag(TAG_SHIP)) {
                    if(contact.hasTag(TAG_SHOT)) {
                        //A shot does 20 damage
                        player.health -= 20
                    }
                    if(contact.hasTag(TAG_BOX)) {
                        val vel = ship.linearVelocity.len2()
                        player.health -= (vel /15).toInt()
                    }
                }

                if(contact.hasTag(TAG_SHOT)) {
                    val shot = contact.bodyForTag(TAG_SHOT)
                    bodiesToDestroy.add(shot)
                }



            }

            override fun endContact(contact: Contact) {
            }

            override fun preSolve(contact: Contact, oldManifold: Manifold?) {
            }

            override fun postSolve(contact: Contact, impulse: ContactImpulse?) {
            }
        })

        ship = world.body {
            type = BodyDef.BodyType.DynamicBody
            userData = TAG_SHIP
            polygon(Vector2(-1f, -1f), Vector2(0f, 1f), Vector2(1f, -1f)) {
                density = 1f
            }
            linearDamping = 1f
            angularDamping = 5f
        }

        val randomFactor = 0f..15f

        for (x in 0..99)
            for (y in 0..99) {
                world.body {
                    type = BodyDef.BodyType.StaticBody
                    userData = TAG_BOX
                    box(2f, 2f, vec2(x * 25f + randomFactor.random(), y * 25f + randomFactor.random()))
                }
            }
    }

    private fun updateBox2D(delta: Float) {

        for (body in bodiesToDestroy)
            world.destroyBody(body)

        bodiesToDestroy.clear()


        val frameTime = delta.coerceAtMost(0.25f)
        accumulator += frameTime
        if (accumulator >= MAX_STEP_TIME) {
            world.step(delta, 6, 2)
            accumulator -= MAX_STEP_TIME
        }
    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(.3f, .5f, .8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
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
