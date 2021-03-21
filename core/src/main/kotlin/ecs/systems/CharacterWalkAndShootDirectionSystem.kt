package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.CharacterSpriteComponent
import ecs.components.PlayerControlComponent
import ktx.ashley.allOf
import physics.Mappers
import tru.SpriteDirection

class CharacterWalkAndShootDirectionSystem :
    IteratingSystem(
        allOf(
            CharacterSpriteComponent::class,
            PlayerControlComponent::class
        ).get(), 10) {

    var characterAngle = 0f
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
        val characterSpriteComponent = Mappers.characterSpriteComponentMapper.get(entity)
        val controlComponet = Mappers.playerControlMapper.get(entity)
        characterAngle = if(controlComponet.moving) controlComponet.walkVector.angleDeg() else controlComponet.aimVector.angleDeg()

        when (characterAngle) {
            in 150f..209f -> characterSpriteComponent.currentDirection = SpriteDirection.East
            in 210f..329f -> characterSpriteComponent.currentDirection = SpriteDirection.North
            in 330f..360f -> characterSpriteComponent.currentDirection = SpriteDirection.West
            in 0f..29f -> characterSpriteComponent.currentDirection = SpriteDirection.West
            in 30f..149f -> characterSpriteComponent.currentDirection = SpriteDirection.South
            else -> characterSpriteComponent.currentDirection = SpriteDirection.South
        }
    }
}