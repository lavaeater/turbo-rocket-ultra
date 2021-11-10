package ecs.components.ai

import com.badlogic.gdx.ai.btree.Task

abstract class TaskComponent : CoolDownComponent() {
    var status : Task.Status = Task.Status.RUNNING
    val isRunning get() = status == Task.Status.RUNNING
    var firstRun = true

    override fun reset() {
        super.reset()
        firstRun = true
        status = Task.Status.RUNNING
    }
}