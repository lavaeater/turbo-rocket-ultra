package systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import components.Box2dSteerable
import components.Remove
import ktx.ashley.allOf
import ktx.ashley.exclude

class SteerSystem: IteratingSystem(allOf(Box2dSteerable::class).exclude(Remove::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val steer = Box2dSteerable.get(entity)
        steer.update(deltaTime)
    }
}
