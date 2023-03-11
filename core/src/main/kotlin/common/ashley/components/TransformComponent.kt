package common.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.math.vec2


class TransformComponent : Component, Pool.Poolable {
    var feelsGravity = false
    val position: Vector2 = vec2()
    var height = 4f
    var verticalSpeed = 0f
    val direction = vec2(1f, 0f) //Unit vector pointing towards Y, for no reason of course

    var angleDegrees: Float
        set(value) {
            direction.setAngleDeg(value)
        }
        get() {
            return direction.angleDeg()
        }

    var angleRadians: Float
        get() {
            return direction.angleRad()
        }
        set(value) {
            direction.setAngleRad(value)
        }

    fun update(body: Body) {
        position.set(body.position)
        angleDegrees = body.angle * MathUtils.radiansToDegrees + 90f
    }

    override fun reset() {
        position.set(Vector2.Zero)
        angleDegrees = 0f
        height = 4f
        verticalSpeed = 0f
    }

    companion object {
        val mapper = mapperFor<TransformComponent>()
        fun get(entity: Entity): TransformComponent {
            return mapper.get(entity)
        }

        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
    }
}

