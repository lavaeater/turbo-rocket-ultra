package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool

class MiniMapComponent: Component, Pool.Poolable {
    var color: Color = Color.GREEN
    var miniMapShape: Shape = Shape.Dot

    override fun reset() {
        color = Color.GREEN
        miniMapShape = Shape.Dot
    }

}