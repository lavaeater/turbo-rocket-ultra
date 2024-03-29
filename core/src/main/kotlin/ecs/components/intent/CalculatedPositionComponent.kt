package ecs.components.intent

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.math.vec2

/**
 * A component for calculating position (transform) of an entity
 * When created, it needs a function that calculates the desired position
 * properly.
 */
class CalculatedPositionComponent : Component, Pool.Poolable {
    val calcPos = vec2()
    var calculate: () -> Vector2 = { Vector2.Zero.cpy()}
    override fun reset() {
        calculate = { Vector2.Zero.cpy() }
        calcPos.setZero()
    }
}

class CalculatedRotationComponent: Component, Pool.Poolable {
    var calculate: ()-> Float = { 0f }
    override fun reset() {
        calculate = { 0f }
    }

}

class FunctionsComponent: Component, Pool.Poolable {
    var functions = mutableMapOf<String, (Entity)->Unit>()
    override fun reset() {
        functions.clear()
    }
}