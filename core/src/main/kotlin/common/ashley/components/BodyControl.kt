package common.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.math.vec2

class BodyControl: Component, Pool.Poolable {
    val directionVector = vec2()
    val aimDirection = vec2()
    var maxForce = 100f
    var currentForce = 0f
    override fun reset() {
        directionVector.set(Vector2.Zero)
        aimDirection.set(Vector2.Zero)
        maxForce = 100f
        currentForce = 0f
    }

    companion object {
        val mapper = mapperFor<BodyControl>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): BodyControl {
            return mapper.get(entity)
        }
    }
}