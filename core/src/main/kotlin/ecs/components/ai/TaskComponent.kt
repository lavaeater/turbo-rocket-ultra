package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.utils.Pool
import ktx.math.random

abstract class CoolDownComponent: Component, Pool.Poolable {
    var coolDownRange = 30f..60f
    var coolDown = coolDownRange.random()
    override fun reset() {
        coolDown = coolDownRange.random()
    }

}

abstract class TaskComponent : CoolDownComponent() {
    var status : Task.Status = Task.Status.RUNNING
    override fun reset() {
        super.reset()
        status = Task.Status.RUNNING
    }
}