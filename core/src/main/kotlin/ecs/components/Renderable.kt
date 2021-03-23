package ecs.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import space.earlygrey.shapedrawer.ShapeDrawer

interface Renderable {
    fun render(position: Vector2, rotation: Float, scale: Float, animationStateTime: Float, batch: Batch, shapeDrawer: ShapeDrawer)
}