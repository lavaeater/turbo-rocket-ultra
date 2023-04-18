package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.ashley.components.TransformComponent
import eater.ecs.ashley.components.character.CharacterComponent
import ecs.components.graphics.TextureRegionComponent
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf

class UpdateCharacterSystem: IteratingSystem(allOf(TransformComponent::class, CharacterComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformComponent = TransformComponent.get(entity)
        val characterComponent = CharacterComponent.get(entity)
        val playerControlComponent = PlayerControlComponent.get(entity)
        characterComponent.worldPosition.set(transformComponent.position)
        characterComponent.angleDegrees = playerControlComponent.aimVector.angleDeg()
        characterComponent.aimVector.set(playerControlComponent.aimVector)
        characterComponent.scale = TextureRegionComponent.get(entity).actualScale
    }
}