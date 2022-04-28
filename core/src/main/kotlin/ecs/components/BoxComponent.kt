package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import space.earlygrey.shapedrawer.ShapeDrawer

class BoxComponent(private val width: Float = 2f, private val height: Float = 2f, private val color: Color = Color.GREEN): Renderable,
    Component {
    override fun render(
        position: Vector2,
        rotation: Float,
        scale: Float,
        animationStateTime: Float,
        batch: Batch,
        shapeDrawer: ShapeDrawer
    ) {
        shapeDrawer.filledRectangle(position.x, position.y, width, height, color)
    }
}