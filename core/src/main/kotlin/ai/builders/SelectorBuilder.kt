package ai.builders

import com.badlogic.gdx.ai.btree.branch.Selector

class SelectorBuilder<T> : CompositeTaskBuilder<T>() {
    override fun build(): Selector<T> = Selector(*tasks.toTypedArray())
}