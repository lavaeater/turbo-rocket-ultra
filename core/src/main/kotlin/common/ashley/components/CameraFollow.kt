package common.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class CameraFollow: Component, Pool.Poolable {
    override fun reset() {

    }

    companion object {
        val mapper = mapperFor<CameraFollow>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): CameraFollow {
            return mapper.get(entity)
        }
    }
}