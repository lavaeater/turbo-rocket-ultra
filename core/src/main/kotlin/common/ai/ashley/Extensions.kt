package common.ai.ashley

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

fun canISeeYouFromHere(from: Vector2, aimVector: Vector2, to: Vector2, fovDeg: Float): Boolean {
    return angleToDeg(from, aimVector, to) < fovDeg / 2
}

fun angleToDeg(from: Vector2, aimVector: Vector2, to: Vector2): Float {
    return MathUtils.acos(
        aimVector.dot(to.cpy().sub(from).nor())
    ) * MathUtils.radiansToDegrees
}