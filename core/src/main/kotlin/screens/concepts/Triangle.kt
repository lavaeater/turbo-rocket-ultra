package screens.concepts

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import ktx.math.minus
import ktx.math.plus
import ktx.math.vec2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * (endPointTwo - endPointOne).angleDeg()(endPointTwo - endPointOne).angleDeg()
 *
 * alpha = arccos((b^2 + c^2 - a^2)/ 2bc)
 * beta = arccos((a^2 + c^2 - b^2)/ 2ac)
 * gamma = arccos((a^2 + b^2 - c^2) / 2ab)
 */
class Triangle(val position: Vector2, val b: Float, val c: Float, minAlpha: Float, maxAlpha: Float) {

    val cornerA = vec2()
    val cornerB = vec2()
    val cornerC = vec2()
    val alphaRange = minAlpha..maxAlpha
    var alpha = 0f

    var aRange = (getAFromAlpha(minAlpha)..getAFromAlpha(maxAlpha))
    var a = (aRange.start + aRange.endInclusive) / 2f

    var rotation = 0f
        set(value) {
            field = value
            update()
        }
    val polygonB = Polygon(
        floatArrayOf(
            cornerC.x, cornerC.y,
            cornerB.x, cornerB.y,
            cornerA.x, cornerA.y
        )
    )

    init {
        tryUpdateAlpha(alphaRange.start)
        update()
        updatePolygon()
    }

    fun updateA(newA: Float) {
        if (newA != a && tryUpdateAlpha(newA)) {
            a = newA
            update()
            updatePolygon()
        }
    }

    fun updateInverseKinematic(target: Vector2) {
        /*
        Now we're getting somewhere.

        We want to set a to some length inside the maxium towards
        the point <target>, and also set
        the rotation of the polygon to be so that the point c and b
        create a line towards it. All of this is accomplishable!

        but how?
        what is maxA? Well, it is of course the maxPossible
         */

        var distance = position.dst(target)
        if(distance < aRange.start) {
            distance = aRange.start
        }
        if(distance > aRange.endInclusive) {
            distance = aRange.endInclusive
        }

        a = distance
        alpha = getAlphaFromA(distance)
        rotation = (target - position).angleDeg() + beta - gamma

        update()
        updatePolygon()
    }

    fun updateSoThatBIsOnLineWithTarget(target: Vector2) {
        var direction = (target - position).nor()
        var distance = position.dst(target)
        if(distance < aRange.start) {
            distance = aRange.start
        }
        if(distance > aRange.endInclusive) {
            distance = aRange.endInclusive
        }
        var potentialB = direction.scl(distance)

    }

    fun getAlphaFromA(potentialA: Float): Float {
        val something = (b.pow(2) + c.pow(2) - potentialA.pow(2)) / (2 * b * c)
        val angle = MathUtils.acos(something) * MathUtils.radiansToDegrees
        return angle
    }

    val gamma: Float
        get() {
            val something = (a.pow(2) + b.pow(2) - c.pow(2)) / (2 * a * b)
            val angle = MathUtils.acos(something) * MathUtils.radiansToDegrees
            return angle
        }

    val beta: Float
        get() {
            val something = (a.pow(2) + c.pow(2) - b.pow(2)) / (2 * a * c)
            val angle = MathUtils.acos(something) * MathUtils.radiansToDegrees
            return angle
        }

    fun getAFromAlpha(potentialAlpha: Float): Float {
        val aFromAlpha =
            sqrt(b.pow(2) + c.pow(2) - (2 * b * c * MathUtils.cos(potentialAlpha * MathUtils.degreesToRadians)))
        return aFromAlpha
    }

    fun tryUpdateAlpha(newA: Float): Boolean {
        val angle = getAlphaFromA(newA)
        if (alphaRange.contains(angle)) {
            alpha = angle
            return true
        }
        return false
    }

    fun update() {
        val vB = vec2(b).rotateAroundDeg(Vector2.Zero, alpha)
        cornerC.set(cornerA + vB)
        cornerB.set(cornerA + vec2(c))
    }

    fun updatePolygon() {
        polygonB.setOrigin(cornerC.x, cornerC.y)
        polygonB.setVertex(2, cornerA.x, cornerA.y)
        polygonB.setVertex(0, cornerC.x, cornerC.y)
        polygonB.setVertex(1, cornerB.x, cornerB.y)
        polygonB.setPosition(position.x - polygonB.originX, position.y - polygonB.originY)
        polygonB.rotation = rotation
    }

    val arms: Array<Pair<Vector2, Vector2>> = arrayOf(Pair(vec2(), vec2()), Pair(vec2(), vec2()))
        get() {
            updateArms(field)
            return field
        }

    private fun updateArms(arms: Array<Pair<Vector2, Vector2>>) {
        polygonB.getVertex(0, arms[0].first)
        polygonB.getVertex(2, arms[0].second)
        polygonB.getVertex(2, arms[1].first)
        polygonB.getVertex(1, arms[1].second)
    }
}