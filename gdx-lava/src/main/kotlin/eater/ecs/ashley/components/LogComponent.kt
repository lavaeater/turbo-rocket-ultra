package eater.ecs.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class LogComponent: Component, Pool.Poolable {
    var logFunction: (Entity) ->Unit = {}
    override fun reset() {
        logFunction = {}
    }

    companion object {
        val mapper = mapperFor<LogComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): LogComponent {
            return mapper.get(entity)
        }
    }
}