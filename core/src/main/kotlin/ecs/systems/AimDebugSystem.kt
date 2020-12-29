package ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import control.ShipControl
import injection.Context
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer

class AimDebugSystem(
    private val shipControl: ShipControl = Context.inject(),
    private val batch: PolygonSpriteBatch = Context.inject(),
    private val camera: OrthographicCamera = Context.inject()
) : EntitySystem() {

    private val region: TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later

        pixmap.dispose()
        TextureRegion(texture, 0, 0, 1, 1)
    }

    private val shapeDrawer = ShapeDrawer(batch, region)

    override fun update(deltaTime: Float) {
        if (shipControl.firing) {
            batch.begin()
            val camVec = vec2(camera.position.x, camera.position.y)
            //shapeDrawer.line(shipControl.mouseVector, camVec)

            batch.end()
        }
    }
}