package ecs.components.ai

import com.badlogic.gdx.ai.btree.Task

abstract class TaskComponent : CoolDownComponent() {
    var status : Task.Status = Task.Status.RUNNING
    override fun reset() {
        super.reset()
        status = Task.Status.RUNNING
    }
}