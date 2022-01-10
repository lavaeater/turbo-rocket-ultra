package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PerimeterObjectiveComponent: Component, Pool.Poolable {
    var useTimer = true
    var timeRequired = 30f
    var timeLeft = timeRequired
    var distance = 25f
    var hasEntered = false
    override fun reset() {
        distance = 25f
        timeLeft = timeRequired
        hasEntered = false
        useTimer = true
    }
}