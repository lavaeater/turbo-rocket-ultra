package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.math.vec2

class AnchorPointsComponent : Component, Pool.Poolable {
    val points = mutableMapOf<String, Vector2>()
    val transformedPoints = mutableMapOf<String, Vector2>()
    var useDirectionVector = false

    override fun reset() {
        points.clear()
        transformedPoints.clear()
        useDirectionVector = false
    }
}


class TransformComponent : Component, Pool.Poolable {
    val position: Vector2 = vec2()
    var rotation = 0f

    override fun reset() {
        position.set(Vector2.Zero)
        rotation = 0f
    }
}

