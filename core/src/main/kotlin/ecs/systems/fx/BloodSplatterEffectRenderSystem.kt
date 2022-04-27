package ecs.systems.fx

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.fx.SplatterComponent
import ktx.ashley.allOf
import ktx.graphics.use
import physics.getComponent

class BloodSplatterEffectRenderSystem(private val batch: Batch) : IteratingSystem(allOf(SplatterComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = entity.getComponent<SplatterComponent>()
        val effect = component.splatterEffect
        if(effect.isComplete) {
            engine.removeEntity(entity)
        }
        if(!component.started) {
            component.started = true
            val emitter = effect.emitters.first()
            emitter.setPosition(component.at.x, component.at.y)
            val amplitude: Float = (emitter.angle.getHighMax() - emitter.angle.getHighMin()) / 2f
            emitter.angle.setHigh(component.rotation + amplitude, component.rotation - amplitude)
            emitter.angle.setLow(component.rotation)
            emitter.start()
        }
        effect.update(deltaTime)
        batch.use {
            effect.draw(batch)
        }
    }
}

