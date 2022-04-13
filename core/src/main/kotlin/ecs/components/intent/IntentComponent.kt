package ecs.components.intent

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

sealed class IntendsTo {
    object DoNothing: IntendsTo()
    object EnterBuildMode : IntendsTo()
    object LeaveBuildMode : IntendsTo()
    object Build : IntendsTo()
}

class IntentComponent: Component, Pool.Poolable {
    var intendsTo: IntendsTo = IntendsTo.DoNothing
    override fun reset() {
        intendsTo = IntendsTo.DoNothing
    }
}

class ModeComponent: Component, Pool.Poolable {
    override fun reset() {
        TODO("Not yet implemented")
    }

}