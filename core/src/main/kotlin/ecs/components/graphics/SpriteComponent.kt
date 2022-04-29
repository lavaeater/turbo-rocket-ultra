package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool
import ecs.systems.graphics.GameConstants

sealed class RenderableType {
    object Effect: RenderableType()
    object Sprite: RenderableType()
}

class RenderableComponent: Component, Pool.Poolable {

    var layer = 0
    var renderableType: RenderableType = RenderableType.Sprite
    override fun reset() {
        layer = 0
        renderableType = RenderableType.Sprite
    }
}

class SpriteComponent: Component, Pool.Poolable {
    var updateSprite: ()->Unit = {}
    var sprite = Sprite().apply { setScale(actualScale) }
        get() {
            updateSprite()
            if(field.scaleX != actualScale)
                field.setScale(actualScale)
            return field
        }
    val extraSprites = mutableMapOf<String, Sprite>()
    val extraSpriteAnchors = mutableMapOf<String, String>()
    var rotateWithTransform = false
    var scale = 1f
    private val actualScale: Float get() = scale * GameConstants.scale
    var isVisible = true
    override fun reset() {
        isVisible = true
        sprite = Sprite()
        extraSprites.clear()
        extraSpriteAnchors.clear()
        rotateWithTransform = false
        updateSprite = {}
    }
}