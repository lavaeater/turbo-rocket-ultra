package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.math.ImmutableVector2
import ktx.math.toMutable
import ktx.math.vec2
import physics.playerControl

class AnchoredRenderable(val relativePosition: ImmutableVector2) : Sprite() {
    var useDirectionVector = true
    val children = mutableListOf<AnchoredRenderable>()
    val actualPosition = vec2()

    fun setActualPosition(worldPosition: Vector2, rotationRad: Float) {
        val rotatedAnchor = relativePosition.withRotationRad(rotationRad).toMutable()
        actualPosition.set(worldPosition).add(rotatedAnchor.x, rotatedAnchor.y / 2)
        for(child in children) {
            child.setActualPosition(actualPosition, rotationRad)
        }
    }
}

class SceneGraphComponent : Component, Pool.Poolable {
    var useDirectionVector = true

    override fun reset() {
        useDirectionVector = true
    }

}

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
    var feelsGravity = false
    val position: Vector2 = vec2()
    var height = 4f
    var verticalSpeed = 0f
    var rotation = 0f

    override fun reset() {
        position.set(Vector2.Zero)
        rotation = 0f
        height = 4f
        verticalSpeed = 0f
    }
}

