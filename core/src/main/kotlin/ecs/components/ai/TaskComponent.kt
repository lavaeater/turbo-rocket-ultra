package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.utils.Pool

abstract class TaskComponent : Component, Pool.Poolable {
    var status : Task.Status = Task.Status.RUNNING
    override fun reset() {
        status = Task.Status.RUNNING
    }
}