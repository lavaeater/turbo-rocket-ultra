package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.gameplay.TransformComponent
import ecs.components.player.WeaponComponent
import ktx.ashley.allOf

class WeaponOrientationSystem: IteratingSystem(allOf(WeaponComponent::class, TransformComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        TODO("Not yet implemented")
    }
}