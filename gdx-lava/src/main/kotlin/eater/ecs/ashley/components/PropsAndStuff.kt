package eater.ecs.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class PropsAndStuff: Component, Pool.Poolable {
    val props = mutableListOf<Prop>()

    fun getHealth(): Prop.FloatProp.Health {
        return props.first { it is Prop.FloatProp.Health } as Prop.FloatProp.Health
    }

    fun getDetectionRadius(): Prop.FloatProp.DetectionRadius {
        return props.first { it is Prop.FloatProp.DetectionRadius } as Prop.FloatProp.DetectionRadius
    }

    override fun reset() {
        props.clear()
    }

    companion object {
        val mapper = mapperFor<PropsAndStuff>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): PropsAndStuff {
            return mapper.get(entity)
        }
    }
}