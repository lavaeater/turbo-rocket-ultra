package ai.utility

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import core.world
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.math.vec2
import physics.getEntity
import physics.isEntity

fun canISeeYouFromHere(from: Vector2, aimVector: Vector2, to: Vector2, fovDeg: Float): Boolean {
    return angleToDeg(from, aimVector, to) < fovDeg / 2
}

fun angleToDeg(from: Vector2, aimVector: Vector2, to: Vector2): Float {
    return MathUtils.acos(
        aimVector.dot(to.cpy().sub(from).nor())
    ) * MathUtils.radiansToDegrees
}

/**
 * Returns true if a raycast from [from] to [targetPos] hits [targetEntity] as the closest fixture.
 * A wall or other physics body in between will cause this to return false.
 */
fun hasLineOfSight(from: Vector2, targetPos: Vector2, targetEntity: Entity): Boolean {
    var lowestFraction = 1f
    var closestFixture: Fixture? = null
    world().rayCast(from, targetPos) { fixture, _, _, fraction ->
        if (fraction < lowestFraction) {
            lowestFraction = fraction
            closestFixture = fixture
        }
        RayCast.CONTINUE
    }
    return closestFixture != null && closestFixture!!.isEntity() && closestFixture!!.getEntity() == targetEntity
}