package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import ecs.components.ai.Investigate
import ecs.components.ai.NoticedSomething
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import physics.getComponent

class InvestigateSystem : IteratingSystem(allOf(Investigate::class, EnemyComponent::class, NoticedSomething::class).get()) {

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = entity.getComponent<Investigate>()
        component.coolDown -= deltaTime

        if (component.status == Task.Status.RUNNING) {
            val enemyComponent = entity.getComponent<EnemyComponent>()
            val notice = entity.getComponent<NoticedSomething>()
            val transformComponent = entity.getComponent<TransformComponent>()

            if(transformComponent.position.dst(notice.noticedWhere) > 2f) {
                val directionVector = notice.noticedWhere.cpy().sub(transformComponent.position).nor()
                enemyComponent.directionVector.set(directionVector)
                enemyComponent.speed = 3.5f
            } else {
                enemyComponent.speed = 1f
                enemyComponent.directionVector.set(Vector2.Zero)
                entity.remove(NoticedSomething::class.java)
            }
        }
        if(component.coolDown <= 0f) {
            component.status = Task.Status.FAILED
        }
    }
}