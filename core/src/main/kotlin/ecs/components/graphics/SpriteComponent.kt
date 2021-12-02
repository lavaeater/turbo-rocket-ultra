package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool

class SpriteComponent: Component, Pool.Poolable {
    var layer = 0
    var sprite = Sprite()
    var extraSprites = mutableMapOf<String, Sprite>()
    var rotateWithTransform = false
    override fun reset() {
        layer = 0
        sprite = Sprite()
        extraSprites.clear()
        rotateWithTransform = false
    }
}