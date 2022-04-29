package spritesheet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class SpriteCategory(val name: String, val renderPriority: Int, var visible: Boolean = true) {

	private var currentTextureIndex = 0

	fun nextTexture() {
		if(currentTextureIndex + 1 == textureFiles.count()) {
			currentTextureIndex = 0
		} else {
			currentTextureIndex++
		}
	}

	fun toggleVisibility() {
		visible = !visible
	}

	val textures = mutableMapOf<String, Texture>()
	val textureRegions = mutableMapOf<String, MutableMap<String, MutableList<TextureRegion>>>()
	val textureFiles = mutableListOf<String>() //We do filenames now, just for testing

	private val currentTextureKey : String get() {
		return if(textureFiles.any())
			textureFiles[currentTextureIndex]
		else
			""
	}

	val currentTexture: Texture? get() {
		return if(currentTextureKey == "")
			null
		else {
			if (!textures.containsKey(currentTextureKey)) {
				textures[currentTextureKey] = Texture(Gdx.files.local(currentTextureKey))
			}
			textures[currentTextureKey]
		}
	}

	fun getTextureRegions(sheet: SheetDef, textureRegion: TextureRegionDef) : List<TextureRegion> {
		if(currentTextureKey == "") return listOf()
		var returnList = textureRegions[currentTextureKey]?.get(textureRegion.name)
		if(returnList != null)
			return returnList

		if(!textureRegions.containsKey(currentTextureKey)) {
			textureRegions[currentTextureKey] = mutableMapOf()
		}
		if(!textureRegions[currentTextureKey]!!.containsKey(textureRegion.name)) {
			textureRegions[currentTextureKey]!![textureRegion.name] = mutableListOf()
			returnList = textureRegions[currentTextureKey]!![textureRegion.name]!!
			for(frameIndex in 0 until textureRegion.frames) {
				returnList.add(
						TextureRegion(
								currentTexture,
								frameIndex * sheet.itemWidth,
								textureRegion.row * sheet.itemHeight,
								sheet.itemWidth,
								sheet.itemHeight))
			}
		}
		return returnList!!
	}
}