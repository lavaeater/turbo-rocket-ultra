package ecs.components.ai.behavior

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Queue
import ecs.components.ai.CoolDownComponent
import ktx.ashley.mapperFor
import ktx.math.random
import map.grid.Coordinate

class ApproachPlayerStateComponent: Component, Pool.Poolable {
    sealed class ApproachPlayerState(val name: String) {
        object NotStarted : ApproachPlayerState("Not started")
    }

    var state: ApproachPlayerState = ApproachPlayerState.NotStarted


    override fun reset() {
        state = ApproachPlayerState.NotStarted
    }

    companion object {
        val mapper = mapperFor<ApproachPlayerStateComponent>()
        fun get(entity: Entity): ApproachPlayerStateComponent {
            return mapper.get(entity)
        }

        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
    }
}