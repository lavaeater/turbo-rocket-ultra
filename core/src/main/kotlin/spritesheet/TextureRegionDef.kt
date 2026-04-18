package spritesheet

/**
 * Defines one animation slot in a combined spritesheet.
 *
 * [row] is the row index in the old combined-sheet format (still used for legacy sheets).
 * [animFileName] is the base name of the per-animation PNG (e.g. "walk") when using the
 * new LPC per-animation-file layout. If null, falls back to row-based lookup.
 */
class TextureRegionDef(
    val name: String,
    val row: Int,
    val frames: Int,
    val firstFrameIdle: Boolean = true,
    val animFileName: String? = null
)
