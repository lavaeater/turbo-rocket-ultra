package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool
import tru.Assets

sealed class Shape {
    object Rectangle: Shape()
    object Dot: Shape()
}

class MiniMapComponent: Component, Pool.Poolable {
    var color: Color = Color.GREEN
    var miniMapShape: Shape = Shape.Dot

    override fun reset() {
        color = Color.GREEN
        miniMapShape = Shape.Dot
    }

}

class TextureComponent: Component, Pool.Poolable {
    var layer: Int = 0
    var texture: OffsetTextureRegion = Assets.dummyRegion
    val extraTextures = mutableMapOf<String, OffsetTextureRegion>()
    var offsetX: Float = 0f
    var offsetY: Float = 0f
    var scale: Float = 1f
    var rotateWithTransform = false

    override fun reset() {
        rotateWithTransform = false
        scale = 1f
        layer = 0
        offsetX = 0f
        offsetY = 0f
        texture = Assets.dummyRegion
        extraTextures.clear()
    }
}