package ecs.components.towers

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.math.vec2

class TargetInRange(val targetPosition: Vector2 = vec2(), val aimTarget: Vector2 = vec2()) : Component, Pool.Poolable {
    override fun reset() {
        targetPosition.setZero()
        aimTarget.setZero()
    }
}