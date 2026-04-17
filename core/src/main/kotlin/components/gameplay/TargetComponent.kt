package components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pool.Poolable

class AnotherTargetComponent: Component, Poolable {
    override fun reset() {

    }
}

class TargetComponent: Component, Poolable {
    override fun reset() {

    }
}