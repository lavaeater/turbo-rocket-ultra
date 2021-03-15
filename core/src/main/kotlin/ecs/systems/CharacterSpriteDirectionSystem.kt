package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ecs.components.CharacterSpriteComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import physics.getComponent
import physics.to360Degrees
import physics.toDegrees
import tru.SpriteDirection

class CharacterSpriteDirectionSystem :
    IteratingSystem(
        allOf(
            CharacterSpriteComponent::class,
            TransformComponent::class).get()) {

    @ExperimentalStdlibApi
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
        val characterSpriteComponent = entity.getComponent<CharacterSpriteComponent>()
        val rotation = entity.getComponent<TransformComponent>().rotation.to360Degrees()

        /**
         * 0 degrees seems to be straight down.
         */

        when (rotation) {
            in 121f..240f -> characterSpriteComponent.currentDirection = SpriteDirection.North
            in 241f..300f -> characterSpriteComponent.currentDirection = SpriteDirection.West
            in 61f..120f -> characterSpriteComponent.currentDirection = SpriteDirection.East
            in 0f..60f -> characterSpriteComponent.currentDirection = SpriteDirection.South
            in 360f..301f -> characterSpriteComponent.currentDirection = SpriteDirection.South
        }
    }
}