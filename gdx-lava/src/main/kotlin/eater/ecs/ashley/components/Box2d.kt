package eater.ecs.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.ashley.mapperFor

class Box2d : Component, Poolable {
    private var _body: Body? = null
    var body: Body
        get() = _body!!
        set(value) {
            _body = value
        }

    override fun reset() {
        _body = null
    }

    companion object {
        private val mapper = mapperFor<Box2d>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }

        fun get(entity: Entity): Box2d {
            return mapper.get(entity)
        }
    }
}

