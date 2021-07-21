package ecs.components.graphics.renderables

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import isometric.polygonFromPos
import space.earlygrey.shapedrawer.ShapeDrawer

class RenderableBox: Renderable {

    var width: Float = 2f
    var height: Float = 2f
    var color: Color = Color.GREEN
    override val renderableType: RenderableType
        get() = RenderableType.Box

    override fun render(
        position: Vector2,
        rotation: Float,
        scale: Float,
        animationStateTime: Float,
        batch: Batch,
        shapeDrawer: ShapeDrawer
    ) {
        shapeDrawer.setColor(color)
        shapeDrawer.filledPolygon(position.polygonFromPos(width, height))
    }
}