package ai.builders

import com.badlogic.gdx.ai.btree.Task

abstract class CompositeTaskBuilder<T>: TaskBuilder<T>() {
    protected val tasks = mutableListOf<Task<T>>()

    fun first(task: Task<T>) {
        tasks.add(0, task)
    }

    fun then(task: Task<T>) {
        add(task)
    }

    fun last(task: Task<T>) {
        add(task)
    }

    override fun add(task: Task<T>) {
        tasks.add(task)
    }

}