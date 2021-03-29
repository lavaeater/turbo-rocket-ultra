package ecs.components.fx

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ecs.components.graphics.Renderable
import space.earlygrey.shapedrawer.ShapeDrawer

class SplatterComponent(
    var life: Float = 20f,
    var color: Color = Color.RED,
    var radius: Float = 1f) : Renderable, Component, Pool.Poolable {

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

    override fun reset() {
        life = 20f
        color = Color.RED
        radius = 1f
    }
}

