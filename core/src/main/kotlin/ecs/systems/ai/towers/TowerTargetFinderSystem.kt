package ecs.systems.ai.towers

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import eater.ecs.ashley.components.AgentProperties
import eater.ecs.ashley.components.TransformComponent
import ecs.components.towers.FindTarget
import ecs.components.towers.TargetInRange
import ktx.ashley.allOf
import eater.physics.addComponent
import eater.physics.getComponent

class TowerTargetFinderSystem : IteratingSystem(allOf(FindTarget::class, TransformComponent::class).get()) {
    private val enemyFamily = allOf(AgentProperties::class, TransformComponent::class).get()
    private val enemies get() = engine.getEntitiesFor(enemyFamily)


    override fun processEntity(entity: Entity, deltaTime: Float) {
        /*
        Tries to find a target within some range of the position given by transformcomponent.
        If Target is found, will add TargetInRange component to Entity
        if not, failure is accomplished
         */
        val findTarget =  entity.getComponent<FindTarget>()
        var closestEnemy: Entity? = null
        var smallestDist = findTarget.radius
        for(enemy in enemies) {
            val enemyPosition = enemy.getComponent<TransformComponent>()
            val dist = enemyPosition.position.dst(entity.getComponent<TransformComponent>().position)
            if(dist < smallestDist) {
                smallestDist = dist
                closestEnemy = enemy
            }
        }
        if(closestEnemy != null) {
            entity.addComponent<TargetInRange> {
                targetPosition.set(closestEnemy.getComponent<TransformComponent>().position)
            }
            findTarget.status = Task.Status.SUCCEEDED
        } else
            findTarget.status = Task.Status.FAILED
    }
}

