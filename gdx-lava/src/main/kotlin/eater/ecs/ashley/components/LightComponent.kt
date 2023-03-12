package eater.ecs.ashley.components

import box2dLight.Light
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class LightComponent: Component, Pool.Poolable {
    lateinit var light: Light
    var color = Color(1f, 1f, 0f, 0.5f)
    var radius = 15f
    override fun reset() {
        radius = 15f
        color = Color(1f, 1f, 0f, 0.5f)
    }

    companion object {
        val mapper = mapperFor<LightComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): LightComponent {
            return mapper.get(entity)
        }
    }
}