package tru

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import space.earlygrey.shapedrawer.ShapeDrawer
import com.badlogic.gdx.graphics.g2d.TextureRegion

import com.badlogic.gdx.graphics.Texture

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.box2d.body
import ktx.box2d.createWorld
import ktx.box2d.polygon
import ktx.graphics.use

const val GAMEWIDTH = 72f
const val GAMEHEIGHT = 48f


/** First screen of the application. Displayed after the application is created.  */
class FirstScreen : Screen {


    private val shapeTexture :TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()
        TextureRegion(texture, 0, 0, 1, 1)
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
        }

    }

    private fun createBodies() {
        TODO("Not yet implemented")
        val ship = world.body {
            polygon(Vector2(-1f, -1f), Vector2(0f,1f), Vector2(1f, -1f)) {
                density = 40f
            }
        }
    }

    override fun render(delta: Float) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        camera.update(true)
        batch.projectionMatrix = camera.combined

        batch.use {
            box2DDebugRenderer.render(world, batch.projectionMatrix)
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