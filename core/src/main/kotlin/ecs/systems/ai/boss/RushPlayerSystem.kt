package ecs.systems.ai.boss

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.TrackingPlayerComponent
import ecs.components.ai.boss.RushPlayer
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import physics.getComponent
import physics.has

class RushPlayerSystem: IteratingSystem(allOf(
    RushPlayer::class,
    EnemyComponent::class,
    TransformComponent::class,
    TrackingPlayerComponent::class).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        /**
         * Try something different this time.
         * This whole ai business is setting us up for having a million
         * gazillion methods and stuff.
         *
         * Perhaps we could just have some kind of method supplied in the
         * component (which could be completely generic) to run and deliver / set
         * the statuses of the Tasks?
         *
         * So, tasks should perhaps start out as FRESH and then progress through
         * different statuses depending on what this particular callback returns?
         *
         */

        val rushPlayer = entity.getComponent<RushPlayer>()
        if(rushPlayer.isRunning) {
            /*
            What does this particular system want to do?

            The boss has their sights set on a particular player.
            They will now try to bumrush the player, performing a
            hard tackle

             */
            val transformComponent = entity.getComponent<TransformComponent>()
            val enemyComponent = entity.getComponent<EnemyComponent>()
            if(rushPlayer.firstRun) {
                if(entity.has<TrackingPlayerComponent>()) {
                    rushPlayer.rushPoint.set(entity.getComponent<TrackingPlayerComponent>().player!!.entity.getComponent<TransformComponent>().position)
                    rushPlayer.previousDistance = rushPlayer.rushPoint.dst(transformComponent.position)
                } else {
                    rushPlayer.status = Task.Status.FAILED
                }
                rushPlayer.firstRun = false
            }
            val currentDistance = rushPlayer.rushPoint.dst(transformComponent.position)
            rushPlayer.status = if(currentDistance < rushPlayer.previousDistance) {
                rushPlayer.previousDistance = currentDistance
                val direction = rushPlayer.rushPoint.cpy().sub(transformComponent.position).nor()
                enemyComponent.directionVector.set(direction)
                enemyComponent.speed = 20f
                Task.Status.RUNNING
            } else {
                enemyComponent.directionVector.setZero()
                enemyComponent.speed = 2.5f
                Task.Status.SUCCEEDED
            }
            //Scenario 1: the boss does not have a trackingplayer component - so the task fails immediately
            //Scenario 2: the distance is shrinking between boss and target point (where the player used to be), task is running
            //Scenario 3: the distance is no longer shrinking, the task has succeeded and the boss stops
        }
    }
}