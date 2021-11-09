package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import ecs.components.BodyComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.BulletComponent
import ecs.components.gameplay.DestroyComponent
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.getComponent
import physics.has
import screens.CounterObject

class BodyDestroyerSystem(private val world: World) : IteratingSystem(
    allOf(
        BodyComponent::class,
        DestroyComponent::class
    ).get(), 10) {

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val bodyComponent = entity.getComponent<BodyComponent>()
        if(entity.has<BulletComponent>()) {
            CounterObject.bulletCount--
        }
        if(entity.has<EnemyComponent>())
            CounterObject.enemyCount--

        world.destroyBody(bodyComponent.body)
        entity.remove<DestroyComponent>()
        engine.removeEntity(entity)
    }

}