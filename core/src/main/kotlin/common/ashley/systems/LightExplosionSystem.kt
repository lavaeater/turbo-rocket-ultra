package common.ashley.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils.norm
import eater.ecs.ashley.components.LightComponent
import eater.ecs.ashley.components.LightExplosion
import eater.ecs.ashley.components.Remove
import eater.physics.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude

class LightExplosionSystem:IteratingSystem(allOf(LightComponent::class, LightExplosion::class).exclude(Remove::class).get()) {
    val randomRange = (7..10)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val explosion = LightExplosion.get(entity)
        if(explosion.timeLeft <= 0f) {
            entity.addComponent<Remove>()
        } else {
            val norm = norm(0f, explosion.explosionTime, explosion.timeLeft)
            val lightComponent = LightComponent.get(entity)
            val light = lightComponent.light
            light.distance = light.distance * norm
            light.setColor(randomRange.random() / 10f, randomRange.random() / 10f, 0f, 1f)
            explosion.timeLeft-= deltaTime
        }
    }
}