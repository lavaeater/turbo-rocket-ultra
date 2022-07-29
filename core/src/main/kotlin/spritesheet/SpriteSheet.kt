package spritesheet

import com.badlogic.gdx.graphics.Texture

sealed class SpriteSheet(val visible: Boolean = true) {
	data class LoadableSpriteSheet(val path: String, val renderPriority: Int) : SpriteSheet()
	data class TextureSpriteSheet(val renderPriority: Int, val path: String, val texture: Texture) : SpriteSheet()
	class EmptySpriteSheet: SpriteSheet(false)
}