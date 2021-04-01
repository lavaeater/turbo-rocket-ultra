package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Circle
import ecs.components.ai.NoticedSomething
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.player.FiredShotsComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class EnemyHearsShotsSystem : IteratingSystem(allOf(FiredShotsComponent::class).get()) {

    private val enemies get() = engine.getEntitiesFor(allOf(EnemyComponent::class).get())
    private val transformMapper = mapperFor<TransformComponent>()
    private val shotsFiredMapper = mapperFor<FiredShotsComponent>()
    private val noticedMapper = mapperFor<NoticedSomething>()
    private val circle = Circle()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val firedShotsComponent = shotsFiredMapper[entity]
        if(firedShotsComponent.queue.isEmpty)
            return

        var index = 0

        while(!firedShotsComponent.queue.isEmpty && index < 10) {
            val fc = firedShotsComponent.queue.removeLast()
            circle.set(fc.x, fc.y, 50f)
            for (enemy in enemies) {
                val enemyPosition = transformMapper[enemy].position
                if(circle.contains(enemyPosition) && (0..2).random()==0) {
                    if(noticedMapper.has(enemy)) {
                        noticedMapper[enemy].noticedWhere.set(fc)
                    } else {
                        enemy.add(engine.createComponent(NoticedSomething::class.java).apply { noticedWhere.set(fc) })
                    }
                }
            }
            index++
        }
    }
}