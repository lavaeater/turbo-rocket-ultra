package common.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class Human: Component, Pool.Poolable {
    override fun reset() {

    }

    companion object {
        val mapper = mapperFor<Human>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): Human {
            return mapper.get(entity)
        }
    }
}