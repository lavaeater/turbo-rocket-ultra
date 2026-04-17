package systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import common.physics.addComponent
import ktx.ashley.allOf
import ktx.ashley.remove
import components.Remove
import components.RemoveAfter

class RemoveAfterSystem: IteratingSystem(allOf(RemoveAfter::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val ra = RemoveAfter.get(entity)
        ra.time -= deltaTime
        if(ra.time < 0f) {
            entity.remove<RemoveAfter>()
            entity.addComponent<Remove>()
        }
    }
}
