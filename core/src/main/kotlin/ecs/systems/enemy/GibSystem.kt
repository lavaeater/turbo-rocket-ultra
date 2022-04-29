package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import ecs.components.BodyComponent
import ecs.components.ai.GibComponent
import ecs.components.gameplay.DestroyComponent
import ktx.ashley.allOf
import ktx.ashley.get
import physics.addComponent
import physics.getComponent

class GibSystem: IteratingSystem(allOf(GibComponent::class).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val gibComponent = entity.getComponent<GibComponent>()
        gibComponent.coolDown -= deltaTime
        if(gibComponent.coolDown <= 0f && !gibComponent.hasStopped) {
            val bodyComponent = entity.getComponent<BodyComponent>()
            bodyComponent.body.linearVelocity = Vector2.Zero.cpy()
            gibComponent.coolDown = 30f
            gibComponent.hasStopped = true
        } else  if(gibComponent.coolDown <= 0f && gibComponent.hasStopped) {
            entity.addComponent<DestroyComponent> {  }
        } else {
            val body = entity.getComponent<BodyComponent>().body
            if(body.linearVelocity.len2() > 4f)
                body.linearVelocity = body.linearVelocity.cpy().setLength(4f)
        }
    }

}