package screens.concepts

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import isometric.toIsoFrom3d
import ktx.math.plus
import ktx.math.vec3
import space.earlygrey.shapedrawer.ShapeDrawer

open class Segment(
    name: String,
    localPosition: Vector3,
    var length: Float,
    val boneDirection3d: Direction3d,
    color: Color
) : Node(name, localPosition, color = color) {
    var boneEnd: Vector3 = vec3()
        private set
        get() {
            field.set(boneDirection3d.forward).scl(length)
            return field
        }

    override fun renderIso(shapeDrawer: ShapeDrawer) {
        super.renderIso(shapeDrawer)
        origin.toIsoFrom3d(position)
        boneDirection3d.renderIso(origin, shapeDrawer, scale)
        destination.toIsoFrom3d(position + boneEnd)
        shapeDrawer.line(origin, destination)
    }

    override fun rotate(q: Quaternion) {
        super.rotate(q)
        boneDirection3d.rotate(q)
    }
}