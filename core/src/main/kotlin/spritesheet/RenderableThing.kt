package spritesheet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import java.util.*

fun Int.clampAround(minValue: Int = 0, maxValue: Int = 360) : Int {
	if(this >= maxValue)
		return this - maxValue
	if(this < minValue)
		return maxValue - this

	return this
}

fun Float.clampAround(minValue: Float = 0f, maxValue: Float = 360f) : Float {
	if(this >= maxValue)
		return this - maxValue
	if(this < minValue)
		return maxValue - this

	return this
}

class RenderableThing(private val spriteSheetDef: SheetDef, spriteSheets: List<SpriteSheet>) :Widget() {

	private val currentSheets = mutableListOf<SpriteSheet.TextureSpriteSheet>()
	private var currentAnim = spriteSheetDef.textureRegionDef.first { it.name == "walksouth"}
	private val textures = mutableMapOf<String, SpriteSheet.TextureSpriteSheet>()
	private var currentRegionsInvalid = true
	private val currentRegions = mutableListOf<List<TextureRegion>>()
	private var localDelta: Float = 0f
	private var frameDelta = 1f / 6f
	private var currentFrame = 1



	init {
		currentSheets.clear()
		for (spriteSheet in spriteSheets.filter { it is SpriteSheet.LoadableSpriteSheet && it.visible }) {
			val s = spriteSheet as SpriteSheet.LoadableSpriteSheet
			val currentTexture =
				SpriteSheet.TextureSpriteSheet(
					s.renderPriority,
					s.path,
					Texture(Gdx.files.local(s.path))
				)
			textures[s.path] = currentTexture
			currentSheets.add(currentTexture)
		}
	}

	fun nextAnim() : String {
		var currentIndex = spriteSheetDef.textureRegionDef.indexOfFirst { it.name == currentAnim.name }
		currentIndex = (currentIndex + 1).clampAround(0, spriteSheetDef.textureRegionDef.size)

		currentAnim = spriteSheetDef.textureRegionDef.elementAt(currentIndex)
		currentRegionsInvalid = true
		return currentAnim.name
	}

	fun updateSprites(spriteSheets: List<SpriteSheet>) {
		currentRegionsInvalid = true
		currentSheets.clear()
		for (spriteSheet in spriteSheets.filter { it is SpriteSheet.LoadableSpriteSheet && it.visible }) {
			val s = spriteSheet as SpriteSheet.LoadableSpriteSheet
			if(!textures.containsKey(s.path))
				textures[s.path] =
					SpriteSheet.TextureSpriteSheet(s.renderPriority, s.path, Texture(Gdx.files.local(s.path)))

			currentSheets.add(textures[s.path]!!)
		}
	}

	private val textureRegionsToRender : List<List<TextureRegion>> get() {
		if(currentRegionsInvalid) {
			currentRegions.clear()
			currentSheets.sortBy { it.renderPriority }

			for (sheet in currentSheets) {
				val firstFrame = if(currentAnim.firstFrameIdle) 1 else 0 //If first frame is an idle anim. We should add idle as a special case somehow... argh
				currentRegions.add((firstFrame until currentAnim.frames).map {
					TextureRegion(
							sheet.texture,
							it * spriteSheetDef.itemWidth,
							currentAnim.row * spriteSheetDef.itemHeight,
							spriteSheetDef.itemWidth,
							spriteSheetDef.itemWidth)
				})
			}
			currentRegionsInvalid = false
		}
		return currentRegions
	}

	override fun act(delta: Float) {
		super.act(delta)
		localDelta += delta
	}

	override fun draw(batch: Batch, parentAlpha: Float) {
		validate()
		if(localDelta > frameDelta) {
			localDelta = 0f
			currentFrame++
			if(currentFrame > 6)
				currentFrame = 1
		}
		for(t in textureRegionsToRender) {
			val texture = t.elementAt(currentFrame)
			batch.draw(texture, x, y ,width, height)
		}
	}

	fun exportSpriteSheet() {
		val texture = currentSheets.first().texture
		val targetPixmap = Pixmap(texture.width, texture.height, Pixmap.Format.RGBA8888)
		for(sheet in currentSheets.sortedBy { it.renderPriority }) {
			val pixMap = Pixmap(Gdx.files.local(sheet.path))
			targetPixmap.drawPixmap(pixMap,0,0)
			pixMap.dispose()
		}

		PixmapIO.writePNG(Gdx.files.local("${UUID.randomUUID()}.png"), targetPixmap)
		targetPixmap.dispose()
	}
}
