package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.renderables.AnimatedCharacterSprite
import ecs.components.graphics.RenderableComponent
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import physics.AshleyMappers
import tru.SpriteDirection


class CharacterWalkAndShootDirectionSystem :
    IteratingSystem(
        allOf(
            RenderableComponent::class,
            PlayerControlComponent::class
        ).get(), 10) {

    var characterAngle = 0f
    val renderableMapper = mapperFor<RenderableComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        /**
         * The difficult thing here is how we know if a character is walking or not... and what the character
         * is "doing" currently.
         *
         * This system does one, and only one thing: sets the direction the character should be
         * facing.
         *
         * The current anim is managed by something else... the input system, obviously
         * The current anim is managed by something else... the input system, obviously
         */
        val characterSprite = renderableMapper.get(entity).renderable as AnimatedCharacterSprite
        val controlComponet = AshleyMappers.playerControlMapper.get(entity)
        characterAngle = if(controlComponet.moving) controlComponet.walkVector.angleDeg() else controlComponet.aimVector.angleDeg()

        when (characterAngle) {
            in 150f..209f -> characterSprite.currentDirection = SpriteDirection.East
            in 210f..329f -> characterSprite.currentDirection = SpriteDirection.North
            in 330f..360f -> characterSprite.currentDirection = SpriteDirection.West
            in 0f..29f -> characterSprite.currentDirection = SpriteDirection.West
            in 30f..149f -> characterSprite.currentDirection = SpriteDirection.South
            else -> characterSprite.currentDirection = SpriteDirection.South
        }
    }
}