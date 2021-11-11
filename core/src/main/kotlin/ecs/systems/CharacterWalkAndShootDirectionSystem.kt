package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import physics.getComponent
import tru.SpriteDirection


class CharacterWalkAndShootDirectionSystem :
    IteratingSystem(
        allOf(
            AnimatedCharacterComponent::class,
            PlayerControlComponent::class
        ).get(), 10) {

    var characterAngle = 0f
    @OptIn(ExperimentalStdlibApi::class)
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
        val characterComponent = entity.getComponent<AnimatedCharacterComponent>()
        val controlComponet = entity.getComponent<PlayerControlComponent>()
        characterAngle = if(controlComponet.aiming) controlComponet.aimVector.angleDeg() else controlComponet.walkVector.angleDeg()
        if(controlComponet.waitsForRespawn) {
            characterComponent.currentDirection = SpriteDirection.South
        } else {
            when (characterAngle) {
                in 150f..209f -> characterComponent.currentDirection = SpriteDirection.East
                in 210f..329f -> characterComponent.currentDirection = SpriteDirection.North
                in 330f..360f -> characterComponent.currentDirection = SpriteDirection.West
                in 0f..29f -> characterComponent.currentDirection = SpriteDirection.West
                in 30f..149f -> characterComponent.currentDirection = SpriteDirection.South
                else -> characterComponent.currentDirection = SpriteDirection.South
            }
        }
    }
}