package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.ai.BehaviorComponent
import ktx.ashley.allOf
import physics.AshleyMappers
import physics.getComponent

class BehaviorTreeSystem : IntervalIteratingSystem(allOf(BehaviorComponent::class).get(), 0.5f) {
    override fun processEntity(entity: Entity) {
        AshleyMappers.behavior.get(entity).tree.step()
    }
}