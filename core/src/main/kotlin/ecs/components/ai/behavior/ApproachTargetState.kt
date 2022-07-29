package ecs.components.ai.behavior

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class ApproachTargetState: Component, Pool.Poolable {

    var targetEntity: Entity? by EntityOrNullDelegate()
    var status: ApproachTargetStatus = ApproachTargetStatus.NotStarted


    override fun reset() {
        status = ApproachTargetStatus.NotStarted
        targetEntity = null
    }

    companion object {
        val mapper = mapperFor<ApproachTargetState>()
        fun get(entity: Entity): ApproachTargetState {
            return mapper.get(entity)
        }

        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
    }
}