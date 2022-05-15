package ecs.components.gameplay

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import ktx.math.ImmutableVector2
import ktx.math.toMutable
import ktx.math.vec2

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