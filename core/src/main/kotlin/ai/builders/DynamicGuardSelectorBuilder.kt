package ai.builders

import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector

class DynamicGuardSelectorBuilder<T>: CompositeTaskBuilder<T>() {
    override fun build(): DynamicGuardSelector<T> = DynamicGuardSelector(*tasks.toTypedArray())
}