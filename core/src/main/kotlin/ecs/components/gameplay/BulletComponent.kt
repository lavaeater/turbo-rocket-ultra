package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class BulletComponent: Component, Pool.Poolable {

    var damage = 0
    override fun reset() {
        damage = 0
    }
}