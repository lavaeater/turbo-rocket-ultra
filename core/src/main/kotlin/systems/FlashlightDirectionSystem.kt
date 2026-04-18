package systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import components.BodyControl
import components.Flashlight
import components.Remove
import components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.math.times


class FlashlightDirectionSystem: IteratingSystem(
    allOf(
        Flashlight::class,
        BodyControl::class,
        TransformComponent::class
    ).exclude(Remove::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val light = Flashlight.get(entity)
        val bodyControl = BodyControl.get(entity)
        val lightPos = TransformComponent.get(entity).position.cpy()
        light.direction.setAngleDeg(bodyControl.aimDirection.angleDeg())
        lightPos.add(light.direction * light.offset)
        light.light.setPosition(lightPos.x, lightPos.y)
        light.light.direction = light.direction.angleDeg()
    }
}
