package graphics

import com.badlogic.gdx.math.Vector2
import graphics.Geometry

class ContainerGeometry(position: Vector2, rotation: Float) : Geometry(position, rotation) {
    override fun updateSelf() {
    }

    fun updateGeometry() {
        update(worldPosition, worldRotation)
    }
}