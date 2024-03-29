package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.core.world
import eater.ecs.ashley.components.Box2d
import ecs.components.fx.GibComponent
import ecs.components.gameplay.DestroyComponent
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.AshleyMappers
import eater.physics.addComponent

class GibSystem : IteratingSystem(allOf(GibComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val gibComponent = AshleyMappers.gib.get(entity)
        gibComponent.coolDown -= deltaTime
        if (gibComponent.coolDown <= 0f && !gibComponent.hasStopped) {
            if (AshleyMappers.body.has(entity)) {
                val bodyComponent = AshleyMappers.body.get(entity)
                world().destroyBody(bodyComponent.body!!)
                entity.remove<Box2d>()
            }
            gibComponent.coolDown = 60f
            gibComponent.hasStopped = true
        } else if (gibComponent.coolDown <= 0f && gibComponent.hasStopped) {
            entity.addComponent<DestroyComponent> { }
        } else {
//            if (AshleyMappers.body.has(entity)) {
//                val body = AshleyMappers.body.get(entity).body!!
//                if (body.linearVelocity.len2() > 4f)
//                    body.linearVelocity = body.linearVelocity.cpy().setLength(4f)
//            }
        }
    }
}