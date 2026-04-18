package spritesheet

import com.badlogic.gdx.graphics.Texture

sealed class SpriteSheet(val visible: Boolean = true) {
    /**
     * A layer that hasn't been loaded yet.
     *
     * [variantFolder] is the absolute directory that contains per-animation PNGs
     * (e.g. `../lpc/spritesheets/body/bodies/male`). Null means the layer uses
     * the old combined-sheet format and [path] points at that single PNG.
     */
    data class LoadableSpriteSheet(
        val path: String,
        val renderPriority: Int,
        val variantFolder: String? = null
    ) : SpriteSheet()

    data class TextureSpriteSheet(
        val renderPriority: Int,
        val path: String,
        val texture: Texture,
        val variantFolder: String? = null
    ) : SpriteSheet()

    class EmptySpriteSheet : SpriteSheet(false)

    /** Sentinel for a file that was referenced but doesn't exist on disk. Cached to avoid repeated filesystem checks. */
    object MissingSpriteSheet : SpriteSheet(false)
}
