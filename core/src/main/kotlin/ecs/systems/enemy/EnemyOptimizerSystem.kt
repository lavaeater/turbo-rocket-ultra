package ecs.systems.enemy

import com.badlogic.ashley.systems.IntervalSystem
import eater.ecs.ashley.components.AgentProperties
import ecs.components.gameplay.DestroyComponent
import ecs.systems.graphics.GameConstants.MAX_ENEMIES
import ktx.ashley.allOf
import eater.physics.addComponent

class EnemyOptimizerSystem : IntervalSystem(5f) {

    private val enemies get() = engine.getEntitiesFor(allOf(AgentProperties::class).get())
    private val count get() = enemies.count()

    override fun updateInterval() {
        if(count > MAX_ENEMIES) {
            for(n in 0..(count - MAX_ENEMIES)) {
                val entity = enemies.random()
                entity.addComponent<DestroyComponent>()
            }
        }
    }
}