package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.Panic
import ecs.components.enemy.EnemyComponent
import ktx.ashley.allOf
import ktx.math.random
import physics.getComponent

class PanicSystem : IteratingSystem(allOf(Panic::class, EnemyComponent::class).get()) {

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = entity.getComponent<Panic>()
        if(component.firstRun) {
            component.firstRun = false
            val directionRange = -1f..1f
            val enemyComponent = entity.getComponent<EnemyComponent>()
            enemyComponent.directionVector.set(directionRange.random(), directionRange.random()).nor()
            enemyComponent.speed = 10f
        }
        if (component.status == Task.Status.RUNNING) {
            component.coolDown -= deltaTime
            if (component.coolDown <= 0f)
                component.status = Task.Status.SUCCEEDED
        }
    }
}

