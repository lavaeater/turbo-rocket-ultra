package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import ecs.components.BodyComponent
import ecs.components.ai.GibComponent
import ecs.components.gameplay.DestroyComponent
import factories.world
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.remove
import physics.AshleyMappers
import physics.addComponent
import physics.getComponent
import physics.has

class GibSystem : IteratingSystem(allOf(GibComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val gibComponent = AshleyMappers.gib.get(entity)
        gibComponent.coolDown -= deltaTime
        if (gibComponent.coolDown <= 0f && !gibComponent.hasStopped) {
            if (AshleyMappers.body.has(entity)) {
                val bodyComponent = AshleyMappers.body.get(entity)
                world().destroyBody(bodyComponent.body!!)
                entity.remove<BodyComponent>()
            }
            gibComponent.coolDown = 60f
            gibComponent.hasStopped = true
        } else if (gibComponent.coolDown <= 0f && gibComponent.hasStopped) {
            entity.addComponent<DestroyComponent> { }
        } else {
            if (AshleyMappers.body.has(entity)) {
                val body = AshleyMappers.body.get(entity).body!!
                if (body.linearVelocity.len2() > 4f)
                    body.linearVelocity = body.linearVelocity.cpy().setLength(4f)
            }
        }
    }
}