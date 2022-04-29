package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

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