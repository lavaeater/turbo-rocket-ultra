package ecs.components.ai.behavior

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Queue
import ecs.components.ai.CoolDownComponent
import ktx.ashley.mapperFor
import ktx.math.random
import map.grid.Coordinate

class AmbleState : CoolDownComponent(), Pool.Poolable {
    init {
        coolDownRange = 0.5f..1f
        coolDown = coolDownRange.random()
    }

    var startPointCoordinate: Coordinate? = null
    var endPointCoordinate: Coordinate? = null
    var wayPoint: Vector2? = null
    val queue = Queue<Vector2>()

    var status: AmbleStatus = AmbleStatus.NotStarted


    override fun reset() {
        super.reset()
        queue.clear()
        endPointCoordinate = null
        wayPoint = null
        status = AmbleStatus.NotStarted
    }

    companion object {
        val mapper = mapperFor<AmbleState>()
        fun get(entity: Entity): AmbleState {
            return mapper.get(entity)
        }

        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
    }
}