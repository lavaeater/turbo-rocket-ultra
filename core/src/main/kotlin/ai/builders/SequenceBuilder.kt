package ai.builders

import com.badlogic.gdx.ai.btree.branch.Sequence

class SequenceBuilder<T> : CompositeTaskBuilder<T>() {
    override fun build(): Sequence<T> = Sequence(*tasks.toTypedArray())
}