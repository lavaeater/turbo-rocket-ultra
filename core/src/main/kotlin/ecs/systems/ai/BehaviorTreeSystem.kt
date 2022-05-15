package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.ai.old.BehaviorComponent
import ktx.ashley.allOf
import physics.behavior

class BehaviorTreeSystem(priority: Int) : IteratingSystem(allOf(BehaviorComponent::class).get(), priority) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        entity?.behavior()?.tree?.step()
    }
}