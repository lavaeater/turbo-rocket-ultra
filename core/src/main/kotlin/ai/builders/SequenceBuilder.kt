package ai.builders

import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.branch.Sequence

class SequenceBuilder<T> : CompositeTaskBuilder<T>() {
    override fun build(): Sequence<T> = Sequence(*tasks.toTypedArray())

    fun moveToNextIfThisSucceeds(task:Task<T>) {
        then(task)
    }
    fun branchSucceedsIfThisSucceeds(task:Task<T>) {
        last(task)
    }
}