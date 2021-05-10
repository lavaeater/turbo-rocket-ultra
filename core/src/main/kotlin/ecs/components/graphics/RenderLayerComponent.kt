package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class RenderLayerComponent : Component, Pool.Poolable {
    var layer: Int = 0
    override fun reset() {
        layer = 0
    }
}