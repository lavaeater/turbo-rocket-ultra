package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.BodyComponent
import ecs.components.EnemyComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import physics.getComponent

class EnemyMovementSystem : IteratingSystem(
    allOf(
        EnemyComponent::class,
        BodyComponent::class,
        TransformComponent::class
    ).get()
) {

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val enemyComponent = entity.getComponent<EnemyComponent>()
        val bodyComponent = entity.getComponent<BodyComponent>()
        moveEnemy(enemyComponent, bodyComponent)
    }

    private fun moveEnemy(enemyComponent: EnemyComponent, bodyComponent: BodyComponent) {
        bodyComponent.body.setLinearVelocity(
            enemyComponent.directionVector.x * enemyComponent.speed,
            enemyComponent.directionVector.y * enemyComponent.speed
        )
    }
}

