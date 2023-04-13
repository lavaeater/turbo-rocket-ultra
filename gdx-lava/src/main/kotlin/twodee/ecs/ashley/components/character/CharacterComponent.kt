package twodee.ecs.ashley.components.character

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool.Poolable
import twodee.input.CardinalDirection
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2
import ktx.ashley.mapperFor

class CharacterComponent : Component, Poolable {

    companion object {
        val mapper = mapperFor<CharacterComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }

        fun get(entity: Entity): CharacterComponent {
            return mapper.get(entity)
        }
    }

    var width: Float = 32f
    var height: Float = 32f
    var scale: Float = 1.0f
    val direction = Direction()
    val worldPosition get() = direction.worldPosition
    val forward = direction.forward

    var angleDegrees
        get() = direction.angleDegrees
        set(value) {
            direction.angleDegrees = value
        }
    val cardinalDirection get() = direction.cardinalDirection

    private val anchors = mapOf(
        CardinalDirection.East to
                mapOf(
                    "rightshoulder" to vec2(-0.25f, 0.5f),
                    "leftshoulder" to vec2(0f, -0.25f),
                    "rightsf" to vec2(0f, 0.5f),
                    "leftsf" to vec2(0.25f, -0.25f)
                ),
        CardinalDirection.South to
                mapOf(
                    "rightshoulder" to vec2(0.25f, 0.5f),
                    "leftshoulder" to vec2(0.25f, -0.5f),
                    "rightsf" to vec2(0.5f, 0.5f),
                    "leftsf" to vec2(0.5f, -0.5f),
                    "rightsb" to vec2(0f, 0.5f),
                    "leftsb" to vec2(0f, -0.5f),
                ),
        CardinalDirection.West to
                mapOf(
                    "rightshoulder" to vec2(0f, 0.25f),
                    "leftshoulder" to vec2(-0.25f, -0.5f)
                ),
        CardinalDirection.North to
                mapOf(
                    "rightshoulder" to vec2(-0.25f, 0.5f),
                    "leftshoulder" to vec2(-0.25f, -0.5f)
                )
    )

    val aimVector: Vector2 = Vector2.X.cpy()

    val worldAnchors
        get() = anchors[CardinalDirection.South]!!.map {
            it.key to worldPosition + vec2().set(it.value.x, it.value.y).times(width * scale)
                .setAngleDeg(direction.angleDegrees - it.value.angleDeg())
        }.toMap()

    override fun reset() {
        worldPosition.setZero()
        angleDegrees = 0f
        aimVector.set(1f, 0f)
        width = 32f
        height = 32f
        scale = 1.0f
    }

}