package ai.builders

import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector
import com.badlogic.gdx.ai.btree.branch.Sequence

class DynamicGuardSelectorBuilder<T>: CompositeTaskBuilder<T>() {
    override fun build(): DynamicGuardSelector<T> = DynamicGuardSelector(*tasks.toTypedArray())
}

class SequenceBuilder<T> : CompositeTaskBuilder<T>() {
    override fun build(): Sequence<T> = Sequence(*tasks.toTypedArray())
}