package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

class BodyComponent : Component, Pool.Poolable {
    lateinit var body: Body
    override fun reset() {
        //No-op, user has to overwrite existing body variable
    }
}