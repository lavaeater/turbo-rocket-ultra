package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class RenderableComponent: Component, Pool.Poolable {

    var layer = 0
    var renderableType: RenderableType = RenderableType.TextureRegion
    override fun reset() {
        layer = 0
        renderableType = RenderableType.TextureRegion
    }
}