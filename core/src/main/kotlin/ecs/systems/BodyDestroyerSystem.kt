package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import eater.ecs.ashley.components.Box2d
import ecs.components.gameplay.DestroyComponent
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.AshleyMappers
import physics.hasUiThing
import physics.uiThing
import screens.CounterObject

class BodyDestroyerSystem(private val world: World) : IteratingSystem(
    allOf(
        DestroyComponent::class
    ).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(AshleyMappers.body.has(entity)) {
            val bodyComponent = AshleyMappers.body.get(entity)
            world.destroyBody(bodyComponent.body)
            entity.remove<Box2d>()
        }
        if(AshleyMappers.bullet.has(entity)) {
            CounterObject.bulletCount--
        }
        if(AshleyMappers.agentProps.has(entity))
            CounterObject.enemyCount--
        if(entity.hasUiThing()) {
            val uiThing = entity.uiThing()
            uiThing.widget.isVisible = false
            uiThing.widget.remove()// .addAction(removeActor())
        }

        entity.removeAll()
        engine.removeEntity(entity)
    }

}