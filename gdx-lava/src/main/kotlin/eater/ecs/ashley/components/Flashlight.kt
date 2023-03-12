package eater.ecs.ashley.components

import box2dLight.Light
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class Flashlight: Component, Pool.Poolable {
    lateinit var light: Light
    var offset = 1f
    val direction = Vector2.X.cpy()
    var directionOffset = 0f
    var on = true
    override fun reset() {
        light.remove(true)
        offset = 1f
        direction.set(Vector2.X)
        directionOffset = 0f
        on = true
    }

    companion object {
        val mapper = mapperFor<Flashlight>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): Flashlight {
            return mapper.get(entity)
        }
    }
}