package spritesheet

import com.badlogic.gdx.Gdx.files
import java.io.FileFilter

class LpcSpriteSheetHelper(val basePath: String = "localfiles/lpc/") {
	/*
	The naming and structure of the LPC folders is a bit... muddled.

	And also different depending on the category

	The categories shall henceforth be known as...

	No, what we shall do is
	1. Find all pngs.
	2. Find keywords determening what category and render priority they have
	3. Find keywords for sex. -if no keyword for sex, it goes in ALL definitions.
	 */

	val spriteSheets = mutableListOf<LpcSpriteSheetDefinition>()
	val categories = listOf(
			LpcSpriteSheetCategoryDefinition("bodies", setOf("body"), 1,
					listOf(
							LpcSpriteSheetCategoryDefinition("noses", setOf("nose"),1),
							LpcSpriteSheetCategoryDefinition("ears", setOf("ears"), 1),
							LpcSpriteSheetCategoryDefinition("eyes",setOf("eyes"),1))),
			LpcSpriteSheetCategoryDefinition("tops", setOf("tops"),2),
			LpcSpriteSheetCategoryDefinition("eyes", setOf("eyes"),2),
			LpcSpriteSheetCategoryDefinition("ears", setOf("ears"),2),
			LpcSpriteSheetCategoryDefinition("noses", setOf("noses"),2),
//			LpcSpriteSheetCategoryDefinition("back", setOf("torso", "back"), 0),
//			LpcSpriteSheetCategoryDefinition("clothes", setOf("torso", "chain"),2),

			LpcSpriteSheetCategoryDefinition("hair", setOf("hair"),2),
			LpcSpriteSheetCategoryDefinition("belts", setOf("belts"),3),
			LpcSpriteSheetCategoryDefinition("accessories", setOf("accessories"),3),
			LpcSpriteSheetCategoryDefinition("shoes", setOf("feet"),2),
			LpcSpriteSheetCategoryDefinition("gloves", setOf("hands"),2),
			LpcSpriteSheetCategoryDefinition("hats & helmets", setOf("hats"),3),
			LpcSpriteSheetCategoryDefinition("pants & skirts", setOf("legs"),2),
			LpcSpriteSheetCategoryDefinition("facial hair", setOf("facial"),2)
	)

	init {
		findSpriteSheets()
	}

	fun findSpriteSheets() {
		findSpriteSheetsRecursive(spriteSheets, basePath)

		for (category in categories)
			categorizeSheets(category, spriteSheets, category.subTags)
	}

	private fun categorizeSheets(
			category: LpcSpriteSheetCategoryDefinition,
			spriteSheets: MutableList<LpcSpriteSheetDefinition>,
			tagsToExclude: Set<String> = emptySet()) {
		category.spriteSheets.addAll(spriteSheets.filter { it.hasTags(category.tags) && it.doesNotHaveTags(tagsToExclude) })

		for(c in category.dependentCategories)
			categorizeSheets(c, spriteSheets, c.subTags)
	}

	private fun findSpriteSheetsRecursive(spriteSheets: MutableCollection<LpcSpriteSheetDefinition>, path:String) {
		val something = files.localStoragePath;
		val dirs = files.local(path).list(FileFilter { it.isDirectory }).map { it.path() }
		for (dir in dirs) {
			findSpriteSheetsRecursive(spriteSheets, dir)
		}
		val files = files.local(path).list("png")
		spriteSheets.addAll(files.map { LpcSpriteSheetDefinition(it.path()) })
	}
}

