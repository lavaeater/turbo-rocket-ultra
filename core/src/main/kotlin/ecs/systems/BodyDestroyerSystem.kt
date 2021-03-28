package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import ecs.components.BodyComponent
import ecs.components.gameplay.DestroyComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.ashley.remove

class BodyDestroyerSystem(private val world: World) : IteratingSystem(
    allOf(
        BodyComponent::class,
        DestroyComponent::class
    ).get(), 10) {

    private val bodyMapper = mapperFor<BodyComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val bodyComponent = bodyMapper.get(entity)!!
        world.destroyBody(bodyComponent.body)
        entity.remove<DestroyComponent>()
        engine.removeEntity(entity)
    }

}