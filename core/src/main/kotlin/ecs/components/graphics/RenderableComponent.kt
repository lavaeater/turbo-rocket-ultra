package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class RenderableComponent: Component, Pool.Poolable {

    companion object {
        val mapper = mapperFor<RenderableComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): RenderableComponent {
            return mapper.get(entity)
        }
    }

    var layer = 0
    var renderableType: RenderableType = RenderableType.Sprite
    override fun reset() {
        layer = 0
        renderableType = RenderableType.Sprite
    }
}