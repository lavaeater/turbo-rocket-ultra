package ai.builders

import com.badlogic.gdx.ai.btree.branch.Parallel

class ParallelBuilder<T> : CompositeTaskBuilder<T>() {
    override fun build(): Parallel<T> = Parallel(*tasks.toTypedArray())
}