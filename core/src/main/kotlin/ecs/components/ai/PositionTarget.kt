package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

open class PositionTarget: Component, Pool.Poolable {
    var position = Vector2.Zero.cpy()
    override fun reset() {
        position = Vector2.Zero.cpy()
    }
}

class AttackPoint: PositionTarget()
class Waypoint : PositionTarget()