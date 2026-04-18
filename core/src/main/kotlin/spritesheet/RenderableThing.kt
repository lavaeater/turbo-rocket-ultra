package spritesheet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import java.util.*

fun Int.clampAround(minValue: Int = 0, maxValue: Int = 360): Int {
    if (this >= maxValue) return this - maxValue
    if (this < minValue) return maxValue - this
    return this
}

fun Float.clampAround(minValue: Float = 0f, maxValue: Float = 360f): Float {
    if (this >= maxValue) return this - maxValue
    if (this < minValue) return maxValue - this
    return this
}

/**
 * Describes one animation slot in the combined export sheet, mapping an LPC per-animation
 * file to specific rows in the output.
 *
 * [animFile] — base name of the source PNG (e.g. "walk"), looked up as `{variantFolder}/{animFile}.png`.
 * [targetStartRow] — first row in the combined sheet for this animation block.
 * [sourceDirectionRows] — which direction rows to read from the source file (0=N,1=W,2=S,3=E).
 * [targetDirectionOffsets] — where each source direction row lands in the target (relative to [targetStartRow]).
 * [frames] — frame columns to copy from source.
 * [targetFrameOffset] — first column in the target row where frames are written.
 */
private data class ExportAnimEntry(
    val animFile: String,
    val targetStartRow: Int,
    val sourceDirectionRows: List<Int>,
    val targetDirectionOffsets: List<Int>,
    val frames: IntRange,
    val targetFrameOffset: Int = 0
)

/**
 * The animation export table: maps LPC per-animation files to combined-sheet rows that
 * match [animation.LpcCharacterAnimDefinition].
 *
 * Sheet layout (64px cells):
 *   rows  4-7  : slash     (6 frames,  4 dirs) — Slash (enemy)
 *   rows  8-11 : walk+idle (9 frames,  4 dirs) — frame 0 = Idle, 1-8 = Walk
 *   rows 16-19 : shoot     (13 frames, 4 dirs) — frames 0-4 StartAim, frame 12 Aiming
 *   row  20    : hurt      (6 frames,  1 dir)  — Death + Hurt (south/universal)
 *   rows 24-27 : run       (8 frames,  4 dirs) — Run
 *   row  28    : climb     (6 frames,  1 dir)  — Climb (south/universal)
 */
private val EXPORT_ANIMS = listOf(
    ExportAnimEntry("slash", 4,  listOf(0, 1, 2, 3), listOf(0, 1, 2, 3), 0..5),
    ExportAnimEntry("walk",  8,  listOf(0, 1, 2, 3), listOf(0, 1, 2, 3), 0..8),
    ExportAnimEntry("shoot", 16, listOf(0, 1, 2, 3), listOf(0, 1, 2, 3), 0..12),
    ExportAnimEntry("hurt",  20, listOf(0), listOf(0), 0..5),
    ExportAnimEntry("run",   24, listOf(0, 1, 2, 3), listOf(0, 1, 2, 3), 0..7),
    ExportAnimEntry("climb", 28, listOf(0), listOf(0), 0..5),
)

/** Combined export sheet size in cells. */
private const val EXPORT_COLS = 13   // widest animation (shoot: 13 frames)
private const val EXPORT_ROWS = 29   // row 28 (climb) + 1
private const val CELL = 64

class RenderableThing(private val spriteSheetDef: SheetDef, spriteSheets: List<SpriteSheet>) : Widget() {

    private val currentSheets = mutableListOf<SpriteSheet.TextureSpriteSheet>()
    private var currentAnim = spriteSheetDef.textureRegionDef.first { it.name == "walksouth" }
    /** Texture cache keyed by file path. Shared between preview and export pixmap loading. */
    private val textures = mutableMapOf<String, SpriteSheet.TextureSpriteSheet?>()
    private val missingPaths = mutableSetOf<String>()
    private var currentRegionsInvalid = true
    private val currentRegions = mutableListOf<List<TextureRegion>>()
    private var localDelta: Float = 0f
    private var frameDelta = 1f / 6f
    private var currentFrame = 1

    init {
        loadSheets(spriteSheets)
    }

    private fun loadSheets(spriteSheets: List<SpriteSheet>) {
        currentSheets.clear()
        for (spriteSheet in spriteSheets.filterIsInstance<SpriteSheet.LoadableSpriteSheet>()) {
            if (spriteSheet.path in missingPaths) continue
            val tss = textures.getOrPut(spriteSheet.path) {
                val file = Gdx.files.local(spriteSheet.path)
                if (!file.exists()) { missingPaths.add(spriteSheet.path); return@getOrPut null }
                SpriteSheet.TextureSpriteSheet(
                    spriteSheet.renderPriority,
                    spriteSheet.path,
                    Texture(file),
                    spriteSheet.variantFolder
                )
            } ?: continue
            currentSheets.add(tss)
        }
    }

    fun nextAnim(): String {
        var idx = spriteSheetDef.textureRegionDef.indexOfFirst { it.name == currentAnim.name }
        idx = (idx + 1).clampAround(0, spriteSheetDef.textureRegionDef.size)
        currentAnim = spriteSheetDef.textureRegionDef.elementAt(idx)
        currentRegionsInvalid = true
        return currentAnim.name
    }

    fun setAnim(name: String) {
        val def = spriteSheetDef.textureRegionDef.firstOrNull { it.name == name } ?: return
        currentAnim = def
        currentRegionsInvalid = true
    }

    fun updateSprites(spriteSheets: List<SpriteSheet>) {
        currentRegionsInvalid = true
        loadSheets(spriteSheets)
    }

    // -------------------------------------------------------------------------
    // Preview rendering
    // -------------------------------------------------------------------------

    private val textureRegionsToRender: List<List<TextureRegion>>
        get() {
            if (currentRegionsInvalid) {
                currentRegions.clear()
                currentSheets.sortBy { it.renderPriority }

                for (sheet in currentSheets) {
                    val regions = if (sheet.variantFolder != null && currentAnim.animFileName != null) {
                        buildPerAnimRegions(sheet, currentAnim)
                    } else {
                        buildCombinedSheetRegions(sheet, currentAnim)
                    }
                    if (regions.isNotEmpty()) currentRegions.add(regions)
                }
                currentRegionsInvalid = false
            }
            return currentRegions
        }

    /**
     * Reads frames from a per-animation PNG:
     * `{variantFolder}/{animFileName}.png`, direction row = `currentAnim.row % 4`.
     */
    private fun buildPerAnimRegions(
        sheet: SpriteSheet.TextureSpriteSheet,
        anim: TextureRegionDef
    ): List<TextureRegion> {
        val animPath = "${sheet.variantFolder}/${anim.animFileName}.png"
        val animTexture = textures.getOrPut(animPath) {
            val file = Gdx.files.local(animPath)
            if (!file.exists()) return emptyList()
            SpriteSheet.TextureSpriteSheet(
                sheet.renderPriority,
                animPath,
                Texture(file),
                sheet.variantFolder
            )
        }?.texture ?: return emptyList()

        val directionRow = anim.row % 4
        val firstFrame = if (anim.firstFrameIdle) 1 else 0
        return (firstFrame until anim.frames).map { frame ->
            TextureRegion(
                animTexture,
                frame * spriteSheetDef.itemWidth,
                directionRow * spriteSheetDef.itemHeight,
                spriteSheetDef.itemWidth,
                spriteSheetDef.itemHeight
            )
        }
    }

    /** Reads frames from a traditional combined spritesheet using absolute row number. */
    private fun buildCombinedSheetRegions(
        sheet: SpriteSheet.TextureSpriteSheet,
        anim: TextureRegionDef
    ): List<TextureRegion> {
        val firstFrame = if (anim.firstFrameIdle) 1 else 0
        return (firstFrame until anim.frames).map { frame ->
            TextureRegion(
                sheet.texture,
                frame * spriteSheetDef.itemWidth,
                anim.row * spriteSheetDef.itemHeight,
                spriteSheetDef.itemWidth,
                spriteSheetDef.itemWidth
            )
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        localDelta += delta
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        validate()
        if (localDelta > frameDelta) {
            localDelta = 0f
            currentFrame++
            if (currentFrame > 6) currentFrame = 1
        }
        for (t in textureRegionsToRender) {
            if (currentFrame < t.size) {
                val texture = t[currentFrame]
                batch.draw(texture, x, y, width, height)
            }
        }
    }

    // -------------------------------------------------------------------------
    // Export
    // -------------------------------------------------------------------------

    /**
     * Exports a combined spritesheet to `localfiles/created/{uuid}.png`.
     *
     * If all selected layers have a [variantFolder] (new LPC layout), the export
     * composites per-animation PNGs into the combined-sheet format that [animation.AnimLoader]
     * and [animation.LpcCharacterAnimDefinition] expect. Layers with a missing animation
     * file are silently skipped for that animation.
     *
     * If no layers have a variantFolder (legacy combined sheets), the original behaviour
     * is preserved: layers are composited pixel-for-pixel.
     */
    /** Exports the sheet and returns the UUID used for the filename. */
    fun exportSpriteSheet(): String {
        val uuid = UUID.randomUUID().toString()
        val perAnimSheets = currentSheets.filter { it.variantFolder != null }
        if (perAnimSheets.isNotEmpty()) {
            exportPerAnimSpriteSheet(perAnimSheets, uuid)
        } else {
            exportLegacySpriteSheet(uuid)
        }
        return uuid
    }

    private fun exportPerAnimSpriteSheet(sheets: List<SpriteSheet.TextureSpriteSheet>, uuid: String) {
        val targetPixmap = Pixmap(EXPORT_COLS * CELL, EXPORT_ROWS * CELL, Pixmap.Format.RGBA8888)

        for (entry in EXPORT_ANIMS) {
            for (sheet in sheets.sortedBy { it.renderPriority }) {
                val animPath = "${sheet.variantFolder}/${entry.animFile}.png"
                val file = Gdx.files.local(animPath)
                if (!file.exists()) continue   // Phase 2.3: graceful fallback

                val srcPixmap = Pixmap(file)
                val srcFrameCount = srcPixmap.width / CELL
                val srcDirCount = srcPixmap.height / CELL

                for (i in entry.sourceDirectionRows.indices) {
                    val srcDir = entry.sourceDirectionRows[i]
                    val tgtDir = entry.targetDirectionOffsets[i]
                    if (srcDir >= srcDirCount) continue  // source doesn't have this direction

                    val tgtRow = entry.targetStartRow + tgtDir
                    for (frame in entry.frames) {
                        val srcFrame = frame.coerceAtMost(srcFrameCount - 1)
                        val tgtCol = entry.targetFrameOffset + frame
                        targetPixmap.drawPixmap(
                            srcPixmap,
                            srcFrame * CELL, srcDir * CELL, CELL, CELL,
                            tgtCol * CELL, tgtRow * CELL, CELL, CELL
                        )
                    }
                }
                srcPixmap.dispose()
            }
        }

        val outDir = Gdx.files.local("localfiles/created")
        if (!outDir.exists()) outDir.mkdirs()
        PixmapIO.writePNG(Gdx.files.local("localfiles/created/$uuid.png"), targetPixmap)
        targetPixmap.dispose()
    }

    private fun exportLegacySpriteSheet(uuid: String) {
        val texture = currentSheets.firstOrNull()?.texture ?: return
        val targetPixmap = Pixmap(texture.width, texture.height, Pixmap.Format.RGBA8888)
        for (sheet in currentSheets.sortedBy { it.renderPriority }) {
            val pixMap = Pixmap(Gdx.files.local(sheet.path))
            targetPixmap.drawPixmap(pixMap, 0, 0)
            pixMap.dispose()
        }
        val outDir = Gdx.files.local("localfiles/created")
        if (!outDir.exists()) outDir.mkdirs()
        PixmapIO.writePNG(Gdx.files.local("localfiles/created/$uuid.png"), targetPixmap)
        targetPixmap.dispose()
    }
}
