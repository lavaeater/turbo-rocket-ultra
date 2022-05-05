package ecs.components.ai

import com.badlogic.ashley.core.Entity
import ecs.components.enemy.AgentProperties

class AlertFriends : TaskComponent() {
    var nextToRunTo: Entity? = null
    val alertedFriends = mutableListOf<AgentProperties>()
    var alertRange = (5..15)
    var numberToAlert = alertRange.random()
    override fun toString(): String {
        return "alert friends"
    }

    override fun reset() {
        numberToAlert = alertRange.random()
        nextToRunTo = null
        alertedFriends.clear()
        super.reset()
    }
}