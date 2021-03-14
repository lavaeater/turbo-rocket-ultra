package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ecs.components.CharacterSpriteComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import physics.getComponent
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
         */
        val characterSpriteComponent = entity.getComponent<CharacterSpriteComponent>()
        val transform = entity.getComponent<TransformComponent>()

        when (transform.rotation.toDegrees()) {
            in 15f..165f -> characterSpriteComponent.currentDirection = SpriteDirection.North
            in 0f..14f -> characterSpriteComponent.currentDirection = SpriteDirection.East
            in 345f..360f -> characterSpriteComponent.currentDirection = SpriteDirection.East
            in 164f..135f -> characterSpriteComponent.currentDirection = SpriteDirection.West
            else -> characterSpriteComponent.currentDirection = SpriteDirection.South
        }
    }
}