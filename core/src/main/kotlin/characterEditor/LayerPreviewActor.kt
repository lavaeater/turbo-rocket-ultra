package characterEditor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.utils.Disposable

/**
 * Shows a single animated preview of an LPC sprite layer.
 *
 * Point [variantFolder] at the directory containing per-animation PNGs
 * (e.g. `../lpc/spritesheets/body/bodies/male`). The actor loads `walk.png`
 * from that folder, extracts the south-direction row (row index 2), and
 * animates the frames. Setting a new folder disposes the old texture.
 *
 * If [variantFolder] is blank the actor renders nothing.
 */
class LayerPreviewActor(
    private val frameSize: Int = 64,
    private val frameDuration: Float = 0.12f
) : Widget(), Disposable {

    private var texture: Texture? = null
    private var frames: Array<TextureRegion> = emptyArray()
    private var stateTime = 0f
    private var loadedFolder = ""

    /** Set this to the variant folder to preview. Triggers a texture reload. */
    var variantFolder: String = ""
        set(value) {
            if (field == value) return
            field = value
            reload()
        }

    private fun reload() {
        dispose()
        frames = emptyArray()
        stateTime = 0f
        if (variantFolder.isBlank()) return

        val file = Gdx.files.internal("$variantFolder/walk.png")
        if (!file.exists()) return

        texture = Texture(file)
        val tex = texture ?: return

        // South direction is row 2 (0=N, 1=W, 2=S, 3=E) in the LPC per-animation sheet
        val southRow = 2
        val frameCount = tex.width / frameSize
        frames = Array(frameCount) { col ->
            TextureRegion(tex, col * frameSize, southRow * frameSize, frameSize, frameSize)
        }
        loadedFolder = variantFolder
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (frames.isNotEmpty()) stateTime += delta
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (frames.isEmpty()) return
        val frameIndex = ((stateTime / frameDuration).toInt() % frames.size)
        val frame = frames[frameIndex]
        batch.draw(frame, x, y, width, height)
    }

    override fun getPrefWidth() = frameSize.toFloat()
    override fun getPrefHeight() = frameSize.toFloat()

    override fun dispose() {
        texture?.dispose()
        texture = null
    }
}
