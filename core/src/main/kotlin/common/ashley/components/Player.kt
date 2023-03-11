package common.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class Player: Component, Pool.Poolable {
    override fun reset() {

    }

    companion object {
        val mapper = mapperFor<Player>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): Player {
            return mapper.get(entity)
        }
    }
}