package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.ai.BehaviorComponent
import ecs.systems.enemy.FitnessTracker
import ktx.ashley.allOf
import physics.behavior

class BehaviorTreeSystem : IteratingSystem(allOf(BehaviorComponent::class).get()) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        entity?.behavior()?.tree?.step()
    }
}