package ecs.components.ai.behavior

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ecs.components.ai.CoolDownComponent
import ktx.ashley.mapperFor

class PanikState : CoolDownComponent(), Pool.Poolable {
    var status: PanikStatus = PanikStatus.NotStarted

    companion object {
        val mapper = mapperFor<PanikState>()
        fun get(entity: Entity): PanikState {
            return mapper.get(entity)
        }

        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
    }
}