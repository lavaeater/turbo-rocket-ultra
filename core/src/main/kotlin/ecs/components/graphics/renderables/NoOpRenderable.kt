package ecs.components.graphics.renderables

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import space.earlygrey.shapedrawer.ShapeDrawer

class NoOpRenderable : Renderable {
    override val renderableType: RenderableType
        get() = RenderableType.NoOp

    override fun render(
        position: Vector2,
        rotation: Float,
        scale: Float,
        animationStateTime: Float,
        batch: Batch,
        shapeDrawer: ShapeDrawer
    ) {
    }
}