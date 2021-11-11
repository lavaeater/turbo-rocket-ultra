package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.Amble
import ecs.components.enemy.EnemyComponent
import ktx.ashley.allOf
import ktx.math.random
import physics.getComponent

class AmblingSystem : IteratingSystem(allOf(Amble::class, EnemyComponent::class).get()) {

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = entity.getComponent<Amble>()
        if(component.firstRun) {
            component.firstRun = false
            val directionRange = -1f..1f
            entity.getComponent<EnemyComponent>().directionVector.set(directionRange.random(), directionRange.random()).nor()
        }
        if (component.status == Task.Status.RUNNING) {
            component.coolDown -= deltaTime
            if (component.coolDown <= 0f)
                component.status = Task.Status.SUCCEEDED
        }
    }
}

