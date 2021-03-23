package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import space.earlygrey.shapedrawer.ShapeDrawer

class SplatterComponent(
    var life: Float = 20f,
    private val color: Color = Color.RED,
    private val radius: Float = 1f) : Renderable, Component {

    override fun render(
        position: Vector2,
        rotation: Float,
        scale:Float,
        animationStateTime: Float,
        batch: Batch,
        shapeDrawer: ShapeDrawer
    ) {
        shapeDrawer.filledCircle(position, radius, color)
    }
}