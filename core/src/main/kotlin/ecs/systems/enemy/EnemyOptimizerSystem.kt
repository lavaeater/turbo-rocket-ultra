package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.DestroyComponent
import ecs.systems.graphics.GameConstants.MAX_ENEMIES
import ktx.ashley.allOf
import physics.addComponent
import screens.GameScreen

class EnemyOptimizerSystem : IntervalSystem(5f) {

    private val enemies get() = engine.getEntitiesFor(allOf(EnemyComponent::class).get())
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