package tru

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import space.earlygrey.shapedrawer.ShapeDrawer
import com.badlogic.gdx.graphics.g2d.TextureRegion

import com.badlogic.gdx.graphics.Texture

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.box2d.body
import ktx.box2d.createWorld
import ktx.box2d.polygon
import ktx.graphics.use

const val GAMEWIDTH = 7f
const val GAMEHEIGHT = 4f


/** First screen of the application. Displayed after the application is created.  */
class FirstScreen : Screen {

    companion object {
        private val MAX_STEP_TIME = 1 / 60f
        private var accumulator = 0f
    }

    private val shapeTexture :TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()
        TextureRegion(texture, 0, 0, 1, 1)
    }

    private val testSprite: Sprite by lazy {
        Sprite(shapeTexture)
    }

    private val batch = PolygonSpriteBatch()
    private val shapeDrawer = ShapeDrawer(batch, shapeTexture)
    private val world = createWorld()
    private val camera = OrthographicCamera()
    private val viewPort = ExtendViewport(GAMEWIDTH, GAMEHEIGHT, camera)

    private val box2DDebugRenderer = Box2DDebugRenderer(true, true, true, true, true, true)
    private var needsInit = true


    override fun show() {
        // Prepare your screen here.
        /*
        Add bodies to the world here?
         */
        if(needsInit) {
            createBodies()
            setupSprite()
            needsInit = false
        }
    }

    private fun setupSprite() {
        testSprite.setSize(100f,100f)
        testSprite.setPosition(0f, 0f)
    }

    private fun createBodies() {
        val ship = world.body {
            polygon(Vector2(-1f, -1f), Vector2(0f,1f), Vector2(1f, -1f)) {
                density = 40f
            }
        }
    }

    private fun updateBox2D(delta:Float) {
        val frameTime = delta.coerceAtMost(0.25f)
        accumulator += frameTime
        if (accumulator >= MAX_STEP_TIME) {
            world.step(delta, 6, 2)
            accumulator -= MAX_STEP_TIME
        }
    }

    override fun render(delta: Float) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        updateBox2D(delta)
        camera.update(true)
        batch.projectionMatrix = camera.combined

        batch.use {
            box2DDebugRenderer.render(world, camera.combined)
            testSprite.draw(batch)
        }
    }

    override fun resize(width: Int, height: Int) {
        // Resize your screen here. The parameters represent the new window size.
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