package systems.enemy

import com.badlogic.ashley.systems.IntervalSystem
import components.AgentProperties
import components.gameplay.DestroyComponent
import ktx.ashley.allOf
import physics.addComponent
import systems.graphics.GameConstants

class EnemyOptimizerSystem : IntervalSystem(5f) {

    private val enemies get() = engine.getEntitiesFor(allOf(AgentProperties::class).get())
    private val count get() = enemies.count()

    override fun updateInterval() {
        if(count > GameConstants.MAX_ENEMIES) {
            for(n in 0..(count - GameConstants.MAX_ENEMIES)) {
                val entity = enemies.random()
                entity.addComponent<DestroyComponent>()
            }
        }
    }
}