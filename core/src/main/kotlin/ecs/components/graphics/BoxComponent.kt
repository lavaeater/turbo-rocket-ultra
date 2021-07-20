package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ecs.components.graphics.Renderable
import isometric.polygonFromPos
import space.earlygrey.shapedrawer.ShapeDrawer

class BoxComponent: Renderable,
    Component, Pool.Poolable {

    var width: Float = 2f
    var height: Float = 2f
    var color: Color = Color.GREEN

    override fun render(
        position: Vector2,
        rotation: Float,
        scale: Float,
        animationStateTime: Float,
        batch: Batch,
        shapeDrawer: ShapeDrawer
    ) {

        //shapeDrawer.filledRectangle(position.x - width / 2 , position.y  - height / 2, width, height, color)
        shapeDrawer.setColor(color)
        shapeDrawer.filledPolygon(position.polygonFromPos(width, height))
    }

    override fun reset() {
        width = 2f
        height = 2f
        color = Color.GREEN
    }
}