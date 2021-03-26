package ai.builders

import com.badlogic.gdx.ai.btree.branch.RandomSequence

class RandomSequenceBuilder<T> : CompositeTaskBuilder<T>() {
    override fun build(): RandomSequence<T> = RandomSequence(*tasks.toTypedArray())
}