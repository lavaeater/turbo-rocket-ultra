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
        characterComponent.worldPosition.set(transformComponent.position)
        characterComponent.angleDegrees = transformComponent.angleDegrees
        characterComponent.aimVector.set(PlayerControlComponent.get(entity).aimVector)
        characterComponent.scale = TextureRegionComponent.get(entity).actualScale
    }
}