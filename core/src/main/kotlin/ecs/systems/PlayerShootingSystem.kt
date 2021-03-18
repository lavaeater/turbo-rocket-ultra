package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import ecs.components.PlayerControlComponent
import ecs.components.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.box2d.RayCast
import ktx.box2d.rayCast

class PlayerShootingSystem:IteratingSystem(allOf(PlayerControlComponent::class, TransformComponent::class).get()) {
    val controlMapper = mapperFor<PlayerControlComponent>()
    val transformMapper = mapperFor<TransformComponent>()
    val world : World by lazy { inject() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = controlMapper[entity]
        if(controlComponent.firing) {
            controlComponent.lastShot += deltaTime
            if (controlComponent.lastShot > controlComponent.rof) {
                controlComponent.lastShot = 0f
                //create raycast to find some targets
                val transform = transformMapper[entity]
                val start = transform.position
                val end = controlComponent.aimVector.cpy().nor().scl(50f)
                world.rayCast(start, end) { fixture, point, normal, fraction ->
                    RayCast.CONTINUE
                }
            }
        }
    }

}