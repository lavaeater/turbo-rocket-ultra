package screens

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import kotlin.properties.Delegates

class PointsCloud : DirtyClass() {
    var worldX by Delegates.observable(0f, ::setDirty)
    var worldY by Delegates.observable(0f, ::setDirty)
    val position = vec2(worldX, worldY)
        get() {
            field.set(worldX, worldY)
            return field
        }
    val points = mutableListOf<Vector2>()

    private val _actualPoints = mutableListOf<Vector2>()
    val actualPoints: List<Vector2>
        get() {
            update()
            return _actualPoints
        }
    var rotation by Delegates.observable(0f, ::setDirty)
    fun rotate(degrees: Float) {
        rotation += degrees
    }

    fun rotateFortyFive() {
        rotate(45f)
    }

    fun addPoint(position: Vector2) {
        points.add(position)
        dirty = true
    }

    fun update() {
        if (dirty) {
            _actualPoints.clear()
            _actualPoints.addAll(points.map { p ->
                vec2(p.x + worldX, p.y + worldY).rotateAroundDeg(
                    position,
                    rotation
                )
            })
            dirty = false
        }
    }
}