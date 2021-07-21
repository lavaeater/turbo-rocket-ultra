package ecs.components.graphics.renderables

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import space.earlygrey.shapedrawer.ShapeDrawer

// Should probably not be poolable
interface Renderable{
    val renderableType: RenderableType

    fun render(position: Vector2, rotation: Float, scale: Float, animationStateTime: Float, batch: Batch, shapeDrawer: ShapeDrawer)
}