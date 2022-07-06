package ecs.components.ai.behavior

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Queue
import ecs.components.ai.CoolDownComponent
import ktx.ashley.mapperFor
import ktx.math.random
import map.grid.Coordinate

class AmbleStateComponent : CoolDownComponent(), Pool.Poolable {
    init {
        coolDownRange = 1f..3f
        coolDown = coolDownRange.random()
    }

    var startPointCoordinate: Coordinate? = null
    var endPointCoordinate: Coordinate? = null
    var wayPoint: Vector2? = null
    val queue = Queue<Vector2>()

    sealed class AmbleState(val name: String) {
        object NotStarted : AmbleState("Not started")
        object FindingTargetCoordinate : AmbleState("Looking For Endpoint")
        object FindingPathToTarget : AmbleState("Finding Path")
        object NeedsWaypoint : AmbleState("Needs Waypoint")
        object MoveToWaypoint : AmbleState("Moving to Waypoint")
    }

    var state: AmbleState = AmbleState.NotStarted


    override fun reset() {
        super.reset()
        queue.clear()
        endPointCoordinate = null
        wayPoint = null
        state = AmbleState.NotStarted
    }

    companion object {
        val mapper = mapperFor<AmbleStateComponent>()
        fun get(entity: Entity): AmbleStateComponent {
            return mapper.get(entity)
        }

        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
    }
}