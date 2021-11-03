package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class TextureComponent: Component, Pool.Poolable {
    var layer: Int = 0
    lateinit var texture: OffsetTextureRegion
    val extraTextures = mutableSetOf<OffsetTextureRegion>()
    var xOffset: Float = 0f
    var yOffset: Float = 0f

    override fun reset() {
        layer = 0
        xOffset = 0f
        yOffset = 0f
        extraTextures.clear()
    }
}