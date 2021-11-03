package ecs.components.graphics

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class OffsetTextureRegion(
    texture: Texture,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    val offsetX: Float,
    val offsetY: Float): TextureRegion(texture, x,y,width,height)