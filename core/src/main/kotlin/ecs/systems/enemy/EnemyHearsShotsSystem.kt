package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Circle
import ecs.components.ai.old.NoticedSomething
import ecs.components.enemy.AgentProperties
import ecs.components.player.FiredShotsComponent
import ktx.ashley.allOf
import physics.AshleyMappers
import physics.addComponent

class EnemyHearsShotsSystem : IteratingSystem(allOf(FiredShotsComponent::class).get()) {

    private val enemies get() = engine.getEntitiesFor(allOf(AgentProperties::class).get())
    private val circle = Circle()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val firedShotsComponent = AshleyMappers.firedShots.get(entity)
        if(firedShotsComponent.queue.isEmpty)
            return

        var index = 0

        while(!firedShotsComponent.queue.isEmpty && index < 10) {
            val fc = firedShotsComponent.queue.removeLast()
            circle.set(fc.first, fc.second)
            for (enemy in enemies) {
                val enemyPosition = AshleyMappers.transform.get(enemy).position
                if(circle.contains(enemyPosition) && (0..2).random()==0) {
                    if(AshleyMappers.noticedSomething.has(enemy)) {
                        AshleyMappers.noticedSomething.get(enemy).noticedWhere.set(fc.first)
                    } else {
                        enemy.addComponent<NoticedSomething> {
                            noticedWhere.set(fc.first)
                        }
                    }
                }
            }
            index++
        }
        firedShotsComponent.queue.clear()
    }
}