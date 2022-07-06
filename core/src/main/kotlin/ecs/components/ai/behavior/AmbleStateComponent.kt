package ecs.components.ai.behavior

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ecs.components.ai.Path
import map.grid.Coordinate

class AmbleStateComponent : Component, Pool.Poolable {

    var endpointCoordinate: Coordinate? = null
    var path: Path? = null
    var targetPosition: Vector2? = null

    sealed class AmbleState(val name: String) {
        object NotStarted : AmbleState("Not started")
        object FindingTargetCoordinate : AmbleState("Looking For Endpoint")
        object FindingPathToTarget : AmbleState("Finding Path")
        object MoveToNextStepOnPath : AmbleState("Finding Path")
    }

    var state: AmbleState = AmbleState.NotStarted


    override fun reset() {
        endpointCoordinate = null
        path = null
        targetPosition = null
        state = AmbleState.NotStarted
    }

}