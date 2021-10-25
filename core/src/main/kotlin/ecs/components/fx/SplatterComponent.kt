package ecs.components.fx

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ecs.components.graphics.renderables.Renderable
import ecs.components.graphics.renderables.RenderableType
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer

class SplatterComponent(
    var life: Float = 20f,
    var color: Color = Color.RED,
    var radius: Float = 1f) : Renderable, Component, Pool.Poolable {
    override val renderableType: RenderableType
        get() = RenderableType.Splatter

    override fun render(
        position: Vector2,
        rotation: Float,
        scale:Float,
        animationStateTime: Float,
        batch: Batch,
        shapeDrawer: ShapeDrawer
    ) {
        shapeDrawer.setColor(color)
        //This might, just might, actually work
        shapeDrawer.filledCircle(position, radius)
    }

    override fun reset() {
        life = 20f
        color = Color.RED
        radius = 1f
    }
}

