package common.ashley.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.ashley.components.Box2dSteerable
import eater.ecs.ashley.components.Remove
import ktx.ashley.allOf
import ktx.ashley.exclude

class SteerSystem: IteratingSystem(allOf(Box2dSteerable::class).exclude(Remove::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val steer = Box2dSteerable.get(entity)
        steer.update(deltaTime)
    }
}
