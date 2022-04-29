package ecs.components.graphics

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class OffsetTextureRegion : TextureRegion {
    constructor(
        texture: Texture,
        x: Int,
        y: Int,
        width: Int,
        height: Int, offsetX: Float = 0f, offsetY: Float = 0f
    ) : super(texture, x, y, width, height) {
        this.offsetX = offsetX
        this.offsetY = offsetY
    }
    constructor(texture: Texture) : super(texture)

    var offsetX: Float = 0f
        private set
    var offsetY: Float = 0f
        private set
}