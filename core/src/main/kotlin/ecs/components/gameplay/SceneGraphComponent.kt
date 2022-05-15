package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class SceneGraphComponent : Component, Pool.Poolable {
    var useDirectionVector = true

    override fun reset() {
        useDirectionVector = true
    }

}