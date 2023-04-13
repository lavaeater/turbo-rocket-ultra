package twodee.ecs.ashley.components.character

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class Direction {
    val worldPosition = vec2()
    val forward = vec2(1f, 0f)
    var backward: Vector2 = vec2()
        get() = field.set(forward).rotateDeg(180f)
        private set
    var left: Vector2 = vec2()
        get() = field.set(forward).rotateDeg(90f)
        private set
    var right: Vector2 = vec2()
        get() = field.set(forward).rotateDeg(-90f)
        private set
    var angleDegrees
        get() = forward.angleDeg()
        set(value) {
            forward.setAngleDeg(value)
        }
    val cardinalDirection get() = CardinalToAngles.angleToCardinal(angleDegrees)
    val cardinalAngle get() = CardinalToAngles.cardinalAbsoluteAngles[cardinalDirection]!!
    val cardinalForward get() = CardinalToAngles.cardinalVectors[cardinalDirection]!!

}