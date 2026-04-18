package spritesheet

/**
 * Describes where animation frames come from for a sprite layer.
 *
 * [Row] is the legacy path: all animations are packed into one combined spritesheet,
 * identified by row number and frame count.
 *
 * [File] is the new LPC path: each animation lives in a separate PNG inside a directory,
 * with 4 direction rows (N=0, W=1, S=2, E=3) per file.
 */
sealed class AnimSource {
    /** Frames come from a specific row in a single combined spritesheet. */
    data class Row(val sheet: String, val row: Int, val frames: Int) : AnimSource()

    /** Frames come from `{dir}/{animName}.png`, with per-direction rows 0-3. */
    data class File(val dir: String, val animName: String, val frames: Int) : AnimSource()
}
