package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import tru.Assets

class InFrustumComponent: Component, Pool.Poolable {
    override fun reset() {
    }

}

class TextureComponent: Component, Pool.Poolable {
    var layer: Int = 0
    var texture: TextureRegion = Assets.dummyRegion
    val extraTextures = mutableMapOf<String, TextureRegion>()
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