package ecs.components.ai.behavior
import kotlin.properties.Delegates

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import eater.core.engine
import ecs.components.ai.CoolDownComponent
import ktx.ashley.mapperFor
import kotlin.reflect.KProperty

class EntityOrNullDelegate {
    private val engine by lazy { engine() }
    private var entity: Entity? = null
    operator fun getValue(thisRef: Component, property: KProperty<*>): Entity? {
        return if(engine.entities.contains(entity))
            entity
        else{
            entity = null
            null
        }
    }

    operator fun setValue(thisRef: Component, property: KProperty<*>, value: Entity?) {
        entity = value
    }
}

class AttackState : CoolDownComponent(), Pool.Poolable {
    private val engine by lazy { engine() }
    private var _targetEntity: Entity? = null
    var targetEntity by EntityOrNullDelegate()
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