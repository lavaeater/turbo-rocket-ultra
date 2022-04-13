package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool

class SpriteComponent: Component, Pool.Poolable {
    var layer = 0
    var updateSprite: ()->Unit = {}
    var sprite = Sprite()
        get() {
            updateSprite()
            return field
        }
    val extraSprites = mutableMapOf<String, Sprite>()
    val extraSpriteAnchors = mutableMapOf<String, String>()
    var rotateWithTransform = false
    var offsetX = 0f
    var offsetY = 0f
    var scale = 1f
    override fun reset() {
        layer = 0
        sprite = Sprite()
        extraSprites.clear()
        extraSpriteAnchors.clear()
        rotateWithTransform = false
        updateSprite = {}
    }
}