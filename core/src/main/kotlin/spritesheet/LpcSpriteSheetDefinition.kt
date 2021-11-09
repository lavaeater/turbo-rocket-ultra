package spritesheet

data class LpcSpriteSheetDefinition(val path: String) {

	val tags: Set<String> get() = path.split("/","_",".").distinct().toSet()
	val isFemale: Boolean get() = tags.any { it.contains("female") }
	val isMale: Boolean get() = tags.any { it.contains("male") }
	val fileName: String get() = path.substringAfterLast("/")
	val name: String get() = fileName.substringBefore(".")
	val onlyName: String get() = name.substringAfter("_")
	val subName: String get() = name.substringBefore("_")
	val isUnisex: Boolean get() = !isFemale && !isMale

	fun hasTags(t: Set<String>) : Boolean {
		return tags.containsAll(t)
	}

	fun hasAnyTags(t: Set<String>) : Boolean {
		return t.any { tags.contains(it) }
	}

	fun doesNotHaveTags(t: Set<String>) : Boolean {
		return tags.intersect(t).isEmpty()
	}

}