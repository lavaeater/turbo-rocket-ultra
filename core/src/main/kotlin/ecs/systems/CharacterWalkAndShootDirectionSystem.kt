package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.player.PlayerControlComponent
import extensions.spriteDirection
import ktx.ashley.allOf
import physics.AshleyMappers
import eater.input.CardinalDirection


class CharacterWalkAndShootDirectionSystem :
    IteratingSystem(
        allOf(
            AnimatedCharacterComponent::class,
            PlayerControlComponent::class
        ).get()) {

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
        val characterComponent = AshleyMappers.animatedCharacter.get(entity)
        val controlComponet = AshleyMappers.playerControl.get(entity)
        if(controlComponet.waitsForRespawn) {
            characterComponent.currentDirection = CardinalDirection.South
        } else {
            characterComponent.currentDirection = if(controlComponet.aiming) controlComponet.aimVector.spriteDirection() else controlComponet.walkVector.spriteDirection()
        }
    }
}