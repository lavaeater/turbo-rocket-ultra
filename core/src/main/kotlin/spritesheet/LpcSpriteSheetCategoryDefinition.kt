package spritesheet

data class LpcSpriteSheetCategoryDefinition(
		val name: String,
		val tags: Set<String>,
		val renderPriority: Int,
		val dependentCategories: List<LpcSpriteSheetCategoryDefinition> = listOf()) {

	val spriteSheets = mutableListOf<LpcSpriteSheetDefinition>()
	val subTags: Set<String> get() = dependentCategories.flatMap { it.tags }.toSet()
}

