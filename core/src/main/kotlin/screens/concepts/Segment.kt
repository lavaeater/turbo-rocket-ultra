package screens.concepts

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import isometric.toIsoFrom3d
import ktx.math.minus
import ktx.math.plus
import ktx.math.vec3
import screens.stuff.toIso
import space.earlygrey.shapedrawer.ShapeDrawer

open class Segment(
    name: String,
    localPosition: Vector3 = vec3(),
    var length: Float = 10f,
    val boneDirection3d: Direction3d = Direction3d(),
    color: Color = Color.LIGHT_GRAY,
    rotationDirections: Map<RotationDirection, ClosedFloatingPointRange<Float>> = RotationDirection.allRotations
) : Node(name, localPosition, color = color, rotationDirections = rotationDirections) {
    init {
        direction = boneDirection3d
    }
    var boneScaled = boneDirection3d.forward.cpy().scl(length)
        private set
        get() {
            return field.set(boneDirection3d.forward).scl(length)
        }
    var boneEnd = vec3()
        private set
        get() {
            return field.set(position).add(boneScaled)
        }

    override var position: Vector3 = vec3()
        set
        get() {
            return when (parent) {
                null -> field.set(localPosition)
                is Segment -> field.set((parent as Segment).boneEnd).add(localPosition)
                else -> field.set(parent!!.position).add(localPosition)
            }
        }

    override fun renderIso(shapeDrawer: ShapeDrawer) {
        super.renderIso(shapeDrawer)
        origin.toIsoFrom3d(position)
        //boneDirection3d.renderIso(origin, shapeDrawer, scale)
        shapeDrawer.filledCircle(origin, 1f, color)

        destination.toIsoFrom3d(boneEnd)
        shapeDrawer.filledTriangle(
            origin - boneDirection3d.left.toIso(),
            origin - boneDirection3d.right.toIso(),
            destination, color
        )
        shapeDrawer.filledCircle(destination, 1f, color)
    }

    override fun rotate(q: Quaternion) {
        super.rotate(q)
        boneDirection3d.rotate(q)
    }
}