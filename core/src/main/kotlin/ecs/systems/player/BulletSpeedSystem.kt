package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.BodyComponent
import ecs.components.gameplay.BulletComponent
import ktx.ashley.allOf
import physics.getComponent

class BulletSpeedSystem: IteratingSystem(allOf(BulletComponent::class).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = entity.getComponent<BodyComponent>()
        val linearVelocity = body.body.linearVelocity
        //body.body.linearVelocity = linearVelocity.setLength(100f)
    }
}