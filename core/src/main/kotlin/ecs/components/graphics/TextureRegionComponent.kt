package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import ecs.systems.graphics.GameConstants
import ktx.ashley.mapperFor

class TextureRegionComponent : Component, Pool.Poolable {

    companion object {
        val mapper = mapperFor<TextureRegionComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }

        fun get(entity: Entity): TextureRegionComponent {
            return mapper.get(entity)
        }
    }

    var updateTextureRegion: () -> Unit = {}

    var textureRegion = TextureRegion()
        get() {
            updateTextureRegion()
            return field
        }
    var originX = 0.5f
    var originY = 0.5f
    val extraTextureRegions = mutableMapOf<String, TextureRegion>()
    val extraSpriteAnchors = mutableMapOf<String, String>()
    var rotateWithTransform = false
    var scale = 1f
    val actualScale: Float get() = scale * GameConstants.SCALE
    var isVisible = true
    override fun reset() {
        isVisible = true
        textureRegion = TextureRegion()
        extraTextureRegions.clear()
        extraSpriteAnchors.clear()
        rotateWithTransform = false
        updateTextureRegion = {}
    }
}