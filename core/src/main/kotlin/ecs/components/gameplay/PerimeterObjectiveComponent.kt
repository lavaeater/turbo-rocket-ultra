package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PerimeterObjectiveComponent: Component, Pool.Poolable {
    var useTimer = true
    var timeRequired = 5f
    var timeLeft = timeRequired
    var distance = 15f
    var hasEntered = false
    var firstEntry = true
    override fun reset() {
        distance = 15f
        timeLeft = timeRequired
        hasEntered = false
        useTimer = true
    }
}