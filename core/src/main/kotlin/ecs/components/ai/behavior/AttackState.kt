package ecs.components.ai.behavior

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ecs.components.ai.CoolDownComponent
import ktx.ashley.mapperFor

class AttackState : CoolDownComponent(), Pool.Poolable {
    var targetEntity: Entity? = null
    var status: AttackStatus = AttackStatus.NotStarted

    override fun reset() {
        status = AttackStatus.NotStarted
        targetEntity = null
    }

    companion object {
        val mapper = mapperFor<AttackState>()
        fun get(entity: Entity): AttackState {
            return mapper.get(entity)
        }

        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
    }
}