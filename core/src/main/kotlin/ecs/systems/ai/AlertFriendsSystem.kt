package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.old.AlertFriends
import ecs.components.ai.old.IsAwareOfPlayer
import ecs.components.ai.old.KnownPosition
import ecs.components.enemy.AgentProperties
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import physics.agentProps
import physics.alertFriends
import physics.isAwareOfPlayer
import physics.transform

class AlertFriendsSystem: IteratingSystem(allOf(
    AlertFriends::class,
    AgentProperties::class,
    TransformComponent::class,
    IsAwareOfPlayer::class,
    KnownPosition::class).get()) {

    private val enemyComponentFamily = allOf(AgentProperties::class).get()
    private val allEnemies get() = engine.getEntitiesFor(enemyComponentFamily)

    override fun processEntity(entity: Entity, deltaTime: Float) {

        /*
        Alert enemies by running to them and when bumping into them handling
        that event, somehow - this is actually already done.

        So, we want the enemy to alert N friends. To do that, it has to run to them.
         */
        val alertFriends = entity.alertFriends()
        if(alertFriends.status == Task.Status.RUNNING) {
            alertFriends.coolDown-= deltaTime
            if(alertFriends.nextToRunTo == null) {
                val closest = allEnemies.filter { !alertFriends.alertedFriends.contains(it.agentProps()) && !it.isAwareOfPlayer() }
                    .minByOrNull { it.transform().position.dst2(entity.transform().position) }
                if (closest != null) {
                    alertFriends.nextToRunTo = entity
                }
            }
            if(alertFriends.nextToRunTo?.isAwareOfPlayer() == true) {
                alertFriends.alertedFriends.add(alertFriends.nextToRunTo!!.agentProps())
                alertFriends.nextToRunTo = null
            } else if(alertFriends.nextToRunTo != null) {
                val toRunTo = alertFriends.nextToRunTo!!.transform().position
                val enemyComponent = entity.agentProps()

                enemyComponent.speed = enemyComponent.rushSpeed
                enemyComponent.directionVector.set(toRunTo).sub(entity.transform().position)
                    .nor()
            }
            if(alertFriends.alertedFriends.count() >= alertFriends.numberToAlert) {
                alertFriends.status = Task.Status.SUCCEEDED
            } else if(alertFriends.coolDown < 0f) {
                alertFriends.status = Task.Status.FAILED
            }
        }
    }
}