package common.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.ashley.mapperFor

class RemoveAfter: Component, Poolable {
    var time = 5f
    override fun reset() {
        time = 5f

    }

    companion object {
        val mapper = mapperFor<RemoveAfter>()
        fun get(entity: Entity): RemoveAfter {
            return mapper.get(entity)
        }

        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
    }
}
