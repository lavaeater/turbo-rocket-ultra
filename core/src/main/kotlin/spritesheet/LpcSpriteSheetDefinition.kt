package spritesheet

data class LpcSpriteSheetDefinition(
    val path: String,
    val displayName: String? = null,
    private val explicitIsMale: Boolean? = null,
    private val explicitIsFemale: Boolean? = null,
    /** Directory containing per-animation PNGs. Null for legacy combined-sheet layers. */
    val variantFolder: String? = null
) {
    val tags: Set<String> get() = path.split("/", "_", ".").distinct().toSet()
    val isFemale: Boolean get() = explicitIsFemale ?: tags.any { it.contains("female") }
    val isMale: Boolean get() = explicitIsMale ?: tags.any { it.contains("male") }
    val fileName: String get() = path.substringAfterLast("/")
    val name: String get() = displayName ?: fileName.substringBefore(".")
    val onlyName: String get() = name.substringAfter("_")
    val subName: String get() = name.substringBefore("_")
    val isUnisex: Boolean get() = !isFemale && !isMale

    fun hasTags(t: Set<String>): Boolean = tags.containsAll(t)
    fun hasAnyTags(t: Set<String>): Boolean = t.any { tags.contains(it) }
    fun doesNotHaveTags(t: Set<String>): Boolean = tags.intersect(t).isEmpty()
}
