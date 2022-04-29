package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.ai.BehaviorComponent
import ktx.ashley.allOf
import physics.getComponent

class BehaviorTreeSystem : IteratingSystem(allOf(BehaviorComponent::class).get()) {

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.getComponent<BehaviorComponent>().tree.step()
    }
}