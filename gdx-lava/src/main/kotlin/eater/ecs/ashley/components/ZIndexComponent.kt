package eater.ecs.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class ZIndexComponent: Component, Pool.Poolable {
    var index = 0
    override fun reset() {
        index = 0
    }

    companion object {
        val mapper = mapperFor<ZIndexComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): ZIndexComponent {
            return mapper.get(entity)
        }
    }
}