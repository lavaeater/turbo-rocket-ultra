package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ecs.components.ai.BehaviorComponent
import ecs.systems.enemy.FitnessTracker
import ktx.ashley.allOf
import physics.behavior

class BehaviorTreeSystem : IntervalIteratingSystem(allOf(BehaviorComponent::class).get(), 0.05f) {
    override fun processEntity(entity: Entity) {
        entity.behavior().tree.step()
        FitnessTracker.saveFitnessDataFor(entity)
    }
}