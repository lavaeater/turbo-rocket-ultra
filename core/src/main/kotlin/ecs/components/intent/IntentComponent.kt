package ecs.components.intent

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class IntentComponent: Component, Pool.Poolable {
    var intendsTo: IntendsTo = IntendsTo.DoNothing
    override fun reset() {
        intendsTo = IntendsTo.DoNothing
    }
}