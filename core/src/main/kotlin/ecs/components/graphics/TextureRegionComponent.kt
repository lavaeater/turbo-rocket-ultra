package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import ecs.systems.graphics.GameConstants

class TextureRegionComponent : Component, Pool.Poolable {
    var updateTextureRegion: () -> Unit = {}

    var textureRegion = TextureRegion()
        get() {
            updateTextureRegion()
            return field
        }
    var originX = 0f
    var originY = 0f
    val extraTextureRegions = mutableMapOf<String, TextureRegion>()
    val extraSpriteAnchors = mutableMapOf<String, String>()
    var rotateWithTransform = false
    var scale = 1f
    val actualScale: Float get() = scale * GameConstants.SCALE
    var isVisible = true
    var drawOrigin = false
    override fun reset() {
        isVisible = true
        textureRegion = TextureRegion()
        extraTextureRegions.clear()
        extraSpriteAnchors.clear()
        rotateWithTransform = false
        updateTextureRegion = {}
    }
}