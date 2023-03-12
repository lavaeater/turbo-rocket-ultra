package eater.ecs.ashley.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.ashley.components.Flashlight
import eater.ecs.ashley.components.Remove
import eater.ecs.ashley.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.math.random
import ktx.math.times

class TransformBasedFlashlightDirectionSystem: IteratingSystem(
    allOf(
        Flashlight::class,
        TransformComponent::class
    ).exclude(Remove::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val light = Flashlight.get(entity)
        val transformComponent = TransformComponent.get(entity)
        val lightPos = transformComponent.position.cpy()
        light.direction.setAngleDeg(transformComponent.angleDegrees + light.directionOffset)
        light.light.distance = (5f..15f).random()
        light.light.color.set((5..10).random()/10f, (5..10).random()/10f, 0f,0.5f)
        lightPos.add(light.direction * light.offset)
        light.light.setPosition(lightPos.x, lightPos.y)
        light.light.direction = light.direction.angleDeg()
    }
}