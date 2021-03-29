package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool

class ParticleComponent : Component, Pool.Poolable {
    var life = 1f
    var color = Color.RED
    override fun reset() {
        life = 1f
        color = Color.RED
    }
}