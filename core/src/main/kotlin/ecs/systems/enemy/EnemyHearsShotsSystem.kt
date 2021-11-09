package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Circle
import ecs.components.ai.NoticedSomething
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.player.FiredShotsComponent
import ktx.ashley.allOf
import physics.addComponent
import physics.getComponent
import physics.has

class EnemyHearsShotsSystem : IteratingSystem(allOf(FiredShotsComponent::class).get()) {

    private val enemies get() = engine.getEntitiesFor(allOf(EnemyComponent::class).get())
    private val circle = Circle()

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val firedShotsComponent = entity.getComponent<FiredShotsComponent>()
        if(firedShotsComponent.queue.isEmpty)
            return

        var index = 0

        while(!firedShotsComponent.queue.isEmpty && index < 10) {
            val fc = firedShotsComponent.queue.removeLast()
            circle.set(fc.x, fc.y, 50f)
            for (enemy in enemies) {
                val enemyPosition = enemy.getComponent<TransformComponent>().position
                if(circle.contains(enemyPosition) && (0..2).random()==0) {
                    if(enemy.has<NoticedSomething>()) {
                        enemy.getComponent<NoticedSomething>().noticedWhere.set(fc)
                    } else {
                        enemy.addComponent<NoticedSomething> {
                            noticedWhere.set(fc)
                        }
                    }
                }
            }
            index++
        }
    }
}