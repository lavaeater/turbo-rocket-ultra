package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import ecs.components.EnemyComponent
import ecs.components.ai.Amble
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.random

class AmblingSystem : IteratingSystem(allOf(Amble::class, EnemyComponent::class).get()) {

    private val mapper = mapperFor<Amble>()
    private val eMapper = mapperFor<EnemyComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = mapper.get(entity)
        if (component.status == Task.Status.RUNNING) {
            if(eMapper[entity].directionVector == Vector2.Zero) {
                val directionRange = -1f..1f
                eMapper[entity].directionVector.set(directionRange.random(), directionRange.random()).nor()
            }
            component.coolDown-= deltaTime
            if(component.coolDown <= 0f)
                component.status = Task.Status.FAILED
        }
    }
}

