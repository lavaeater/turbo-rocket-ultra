package tru

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import space.earlygrey.shapedrawer.ShapeDrawer
import com.badlogic.gdx.graphics.g2d.TextureRegion

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.box2d.body
import ktx.box2d.createWorld
import ktx.box2d.polygon
import ktx.graphics.center
import ktx.graphics.use

const val GAMEWIDTH = 720f
const val GAMEHEIGHT = 480f


/** First screen of the application. Displayed after the application is created.  */
class FirstScreen : Screen {

    companion object {
        private val MAX_STEP_TIME = 1 / 60f
        private var accumulator = 0f
    }

    private val shapeTexture :TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(1, 1)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()
        TextureRegion(texture, 1, 1)
    }

    private val batch = SpriteBatch()
    private val shapeDrawer = ShapeDrawer(batch, shapeTexture)
    private val world = createWorld()
    private val camera = OrthographicCamera()
    private val viewPort = ExtendViewport(GAMEWIDTH, GAMEHEIGHT, camera)

    private val box2DDebugRenderer = Box2DDebugRenderer()
    private var needsInit = true


    override fun show() {
        if(needsInit) {
            createBodies()
            camera.setToOrtho(true, viewPort.maxWorldWidth, viewPort.maxWorldHeight)
            needsInit = false
        }
    }

    private fun createBodies() {
        val ship = world.body {
            polygon(Vector2(-10f, -10f), Vector2(0f,10f), Vector2(10f, -10f)) {
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

//        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        updateBox2D(delta)
        camera.update(true)
        batch.projectionMatrix = camera.combined
        batch.use {
        }
        box2DDebugRenderer.render(world, camera.combined)
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