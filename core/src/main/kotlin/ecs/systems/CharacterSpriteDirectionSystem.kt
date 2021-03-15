package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.CharacterSpriteComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import physics.Mappers
import physics.to360Degrees
import tru.SpriteDirection

class CharacterSpriteDirectionSystem :
    IteratingSystem(
        allOf(
            CharacterSpriteComponent::class,
            TransformComponent::class).get(), 10) {



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

        when (Mappers.transformMapper.get(entity).rotation.to360Degrees()) {
            in 121f..240f -> characterSpriteComponent.currentDirection = SpriteDirection.North
            in 241f..300f -> characterSpriteComponent.currentDirection = SpriteDirection.West
            in 61f..120f -> characterSpriteComponent.currentDirection = SpriteDirection.East
            in 0f..60f -> characterSpriteComponent.currentDirection = SpriteDirection.South
            in 360f..301f -> characterSpriteComponent.currentDirection = SpriteDirection.South
        }
    }
}