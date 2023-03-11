package common.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class EntityPropertyComponent: Component, Pool.Poolable {
    val props = mutableMapOf<PropertyName, SimpleProperty>()

    fun getProp(propertyName: PropertyName): SimpleProperty {
        return props[propertyName]!!
    }

    override fun reset() {
        props.clear()
    }

    companion object {
        val mapper = mapperFor<EntityPropertyComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): EntityPropertyComponent {
            return mapper.get(entity)
        }
    }
}