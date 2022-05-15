package ai.builders

import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.branch.Selector

class SelectorBuilder<T> : CompositeTaskBuilder<T>() {
    override fun build(): Selector<T> = Selector(*tasks.toTypedArray())
    fun moveToNextIfThisFails(task:Task<T>) {
        then(task)
    }

    fun expectFailureAndMoveToNext(task:Task<T>) {
        then(task)
    }

    fun expectSuccess(task:Task<T>) {
        then(task)
    }



    fun failBranchIfThisFails(task: Task<T>) {
        last(task)
    }
}