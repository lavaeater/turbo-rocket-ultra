package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.ai.BehaviorComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class BehaviorTreeSystem : IntervalIteratingSystem(allOf(BehaviorComponent::class).get(), .1f) {
    val mapper = mapperFor<BehaviorComponent>()
    override fun processEntity(entity: Entity) {
        mapper[entity].tree.step()
    }

//    override fun processEntity(entity: Entity, deltaTime: Float) {
//        mapper[entity].tree.step()
//    }
}