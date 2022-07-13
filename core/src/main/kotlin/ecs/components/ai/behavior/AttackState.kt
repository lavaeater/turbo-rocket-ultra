package ecs.components.ai.behavior

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import eater.core.engine
import ecs.components.ai.CoolDownComponent
import ktx.ashley.mapperFor

class AttackState : CoolDownComponent(), Pool.Poolable {
    private val engine by lazy { engine() }
    private var _targetEntity: Entity? = null
    var targetEntity: Entity?
        get() {
            if(!engine.entities.contains(_targetEntity))
                _targetEntity = null
            return _targetEntity
        }
        set(value) {
            _targetEntity = value
        }
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