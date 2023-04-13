package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import eater.input.Transform

class NewTransformComponent: Transform(), Component, Pool.Poolable {

    override fun reset() {
        position.setZero()
        forward.setZero()
        aimVector.setZero()

    }

}