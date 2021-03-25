package ai.builders

import com.badlogic.gdx.ai.btree.branch.RandomSelector

class RandomSelectorBuilder<T> : CompositeTaskBuilder<T>() {
    override fun build(): RandomSelector<T> = RandomSelector(*tasks.toTypedArray())
}