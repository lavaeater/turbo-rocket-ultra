package eater.ecs.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class KeyboardAndMouseInput: Component, Pool.Poolable {
    override fun reset() {

    }

    companion object {
        val mapper = mapperFor<KeyboardAndMouseInput>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): KeyboardAndMouseInput {
            return mapper.get(entity)
        }
    }
}