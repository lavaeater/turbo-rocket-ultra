package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.utils.Pool
import ktx.math.random

abstract class TaskComponent : Component, Pool.Poolable {
    var coolDownRange = 30f..60f
    var coolDown = coolDownRange.random()
    var status : Task.Status = Task.Status.RUNNING
    override fun reset() {
        status = Task.Status.RUNNING
        coolDown = coolDownRange.random()
    }
}