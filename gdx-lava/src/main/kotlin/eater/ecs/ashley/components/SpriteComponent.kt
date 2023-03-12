package eater.ecs.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class SpriteComponent: Component, Pool.Poolable {

    var sprite = Sprite()
    var zIndex = 0
    var shadow = false
    var scale = 1f
    var faux3d = false

    override fun reset() {
        zIndex = 0
        sprite = Sprite()
        shadow = false
        scale = 1f
        faux3d = false
    }

    companion object {
        val mapper = mapperFor<SpriteComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): SpriteComponent {
            return mapper.get(entity)
        }
    }
}

