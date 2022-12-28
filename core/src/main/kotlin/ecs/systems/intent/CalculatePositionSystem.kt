package ecs.systems.intent

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.ashley.components.TransformComponent
import ecs.components.intent.CalculatedPositionComponent
import ecs.components.intent.CalculatedRotationComponent
import ecs.components.intent.FunctionsComponent
import ktx.ashley.allOf
import physics.getCalculatedPosition
import physics.getCalculatedRotation
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

class CalculateRotationSystem: IteratingSystem(allOf(CalculatedRotationComponent::class, TransformComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.transform().angleDegrees = entity.getCalculatedRotation()
    }

}

class RunFunctionsSystem: IteratingSystem(allOf(FunctionsComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.runFunctions()
    }
}