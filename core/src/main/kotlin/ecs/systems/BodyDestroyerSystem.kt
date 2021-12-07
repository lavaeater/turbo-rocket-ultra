package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import ecs.components.BodyComponent
import ecs.components.gameplay.DestroyComponent
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.AshleyMappers
import screens.CounterObject

class BodyDestroyerSystem(private val world: World) : IteratingSystem(
    allOf(
        DestroyComponent::class
    ).get()) {

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(AshleyMappers.body.has(entity)) {
            val bodyComponent = AshleyMappers.body.get(entity)
            world.destroyBody(bodyComponent.body)
            entity.remove<BodyComponent>()
        }
        if(AshleyMappers.bullet.has(entity)) {
            CounterObject.bulletCount--
        }
        if(AshleyMappers.enemy.has(entity))
            CounterObject.enemyCount--

        entity.removeAll()
        engine.removeEntity(entity)
    }

}