package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import tru.AnimState
import tru.LpcCharacterAnim
import eater.input.CardinalDirection
import ktx.ashley.mapperFor

class AnimatedCharacterComponent : Component, Pool.Poolable {

    companion object {
        val mapper = mapperFor<AnimatedCharacterComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }

        fun get(entity: Entity): AnimatedCharacterComponent {
            return mapper.get(entity)
        }
    }

    var anims: Map<AnimState, LpcCharacterAnim<TextureRegion>> = emptyMap()
        set(value) {
            if (value.any())
                currentAnim = value.values.first().animations.values.first()
            field = value
        }
    var currentDirection: CardinalDirection = CardinalDirection.South
    var currentAnimState: AnimState = AnimState.Idle
    lateinit var currentAnim: Animation<TextureRegion>
    override fun reset() {
        anims = emptyMap()
        currentAnimState = AnimState.Idle
    }
}