package ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import injection.Context.inject
import ecs.components.ControlComponent
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer

class AimDebugSystem(
    private val controlComponent: ControlComponent = inject(),
    private val batch: PolygonSpriteBatch = inject(),
    private val camera: OrthographicCamera = inject()
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
        if (controlComponent.firing) {
            batch.begin()
            val camVec = vec2(camera.position.x, camera.position.y)
            //shapeDrawer.line(shipControl.mouseVector, camVec)

            batch.end()
        }
    }
}