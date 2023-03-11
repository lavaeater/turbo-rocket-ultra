package common.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.ashley.mapperFor

class Remove : Component, Poolable {
    override fun reset() {

    }
    companion object {
        val mapper = mapperFor<Remove>()
        fun has(entity: Entity):Boolean = mapper.has(entity)
        fun get(entity: Entity): Remove = mapper.get(entity)
    }
}