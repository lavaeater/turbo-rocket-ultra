package ecs.systems.intent

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.gameplay.TransformComponent
import ecs.components.intent.CalculatedPositionComponent
import ecs.components.intent.FunctionsComponent
import ktx.ashley.allOf
import physics.getCalculatedPosition
import physics.runFunctions
import physics.transform

/**
 * An IteratingSystem that sets TransformComponent.position to the return
 * value of the getCalculatedPosition function.
 */
class CalculatePositionSystem : IteratingSystem(allOf(CalculatedPositionComponent::class, TransformComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.transform()
        transform.position.set(entity.getCalculatedPosition())
    }
}

class RunFunctionsSystem: IteratingSystem(allOf(FunctionsComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.runFunctions()
    }
}